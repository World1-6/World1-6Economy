package com.andrew121410.mc.world16economy.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BankManager {

    private final World16Economy plugin;
    private final BankStorage storage;

    // accountUUID → account
    private final Map<UUID, BankAccount> accounts = new LinkedHashMap<>();

    // tierLevel → tier
    private final Map<Integer, BankTier> tiers = new LinkedHashMap<>();

    public BankManager(World16Economy plugin) {
        this.plugin = plugin;
        this.storage = new BankStorage(plugin);
        loadAll();
        startPayrollScheduler();
    }

    // ---- Loading ----

    private void loadAll() {
        // Load tiers first — seed defaults if none exist
        List<BankTier> loadedTiers = storage.loadAllTiers();
        if (loadedTiers.isEmpty()) {
            seedDefaultTiers();
        } else {
            loadedTiers.forEach(t -> tiers.put(t.getLevel(), t));
        }

        // Load accounts with their balances, members, and payroll
        for (BankAccount account : storage.loadAllAccounts()) {
            storage.loadBalancesInto(account);
            if (account.isBusiness()) {
                storage.loadMembersInto(account);
                storage.loadPayrollInto(account);
            }
            accounts.put(account.getAccountUUID(), account);
        }
    }

    private void seedDefaultTiers() {
        List<BankTier> defaults = List.of(
                new BankTier(1, "Basic",    500,    10_000,   1_000),
                new BankTier(2, "Standard", 2_000,  100_000,  10_000),
                new BankTier(3, "Premium",  10_000, -1,       -1)
        );
        defaults.forEach(t -> {
            tiers.put(t.getLevel(), t);
            storage.saveTier(t);
        });
    }

    // ---- Account creation ----

    public BankAccount createAccount(UUID ownerUUID, String name, BankAccountType type) {
        BankAccount account = new BankAccount(UUID.randomUUID(), name, ownerUUID, type, 1);
        accounts.put(account.getAccountUUID(), account);
        storage.saveAccount(account);
        storage.saveMember(account.getAccountUUID(), account.getMembers().get(ownerUUID));
        return account;
    }

    public void deleteAccount(BankAccount account) {
        accounts.remove(account.getAccountUUID());
        storage.deleteAccount(account.getAccountUUID());
    }

    // ---- Saving ----

    public void saveAccount(BankAccount account) {
        storage.saveAccount(account);
        account.getBalances().forEach((currency, balance) ->
                storage.saveBalance(account.getAccountUUID(), currency, balance));
        account.getMembers().values().forEach(m ->
                storage.saveMember(account.getAccountUUID(), m));
        if (account.getScheduledPayroll() != null) {
            storage.savePayroll(account.getScheduledPayroll());
        }
    }

    public void saveBalances(BankAccount account) {
        account.getBalances().forEach((currency, balance) ->
                storage.saveBalance(account.getAccountUUID(), currency, balance));
    }

    public void saveMember(BankAccount account, BankMember member) {
        storage.saveMember(account.getAccountUUID(), member);
    }

    public void deleteMember(BankAccount account, UUID playerUUID) {
        account.getMembers().remove(playerUUID);
        storage.deleteMember(account.getAccountUUID(), playerUUID);
    }

    public void savePayroll(BankAccount account) {
        if (account.getScheduledPayroll() != null) {
            storage.savePayroll(account.getScheduledPayroll());
        } else {
            storage.deletePayroll(account.getAccountUUID());
        }
    }

    public void saveTier(BankTier tier) {
        storage.saveTier(tier);
    }

    // ---- Transactions ----

    public void recordTransaction(BankAccount account, BankTransaction.Type type, UUID currencyUUID, double amount, UUID actorUUID) {
        BankTransaction tx = new BankTransaction(
                UUID.randomUUID(), account.getAccountUUID(), type, currencyUUID, amount, actorUUID,
                System.currentTimeMillis());
        storage.saveTransaction(tx);
    }

    public List<BankTransaction> getTransactions(BankAccount account) {
        return storage.loadTransactions(account.getAccountUUID());
    }

    // ---- Payroll ----

    private void startPayrollScheduler() {
        // Check every 30 seconds
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () ->
                Bukkit.getScheduler().runTask(plugin, this::tickPayroll), 600L, 600L);
    }

    public void tickPayroll() {
        for (BankAccount account : accounts.values()) {
            if (!account.isBusiness()) continue;
            BankScheduledPayroll payroll = account.getScheduledPayroll();
            if (payroll == null || !payroll.isDue()) continue;
            runPayroll(account, null); // null = scheduled (no manual actor)
        }
    }

    /**
     * Runs payroll for a business account. actorUUID is null for scheduled runs, or the
     * player UUID who clicked "Run Now".
     */
    public void runPayroll(BankAccount account, UUID actorUUID) {
        BankScheduledPayroll payroll = account.getScheduledPayroll();
        if (payroll == null) return;

        UUID currencyUUID = payroll.getCurrencyUUID();
        BankTier tier = getTier(account.getTierLevel());

        for (BankMember member : account.getMembers().values()) {
            if (member.getRole() == BankRole.OWNER || member.getRole() == BankRole.VIEWER) continue;
            if (member.getWage() <= 0) continue;
            if (payroll.isOnlineOnly() && Bukkit.getPlayer(member.getPlayerUUID()) == null) continue;

            double wage = member.getWage();

            // Check transaction limit
            if (tier != null && tier.hasTransactionLimit() && wage > tier.getTransactionLimit()) {
                notifyOwner(account, "<red>⚠ Payroll: wage for <white>" + member.getPlayerUUID()
                        + " <red>exceeds transaction limit.");
                continue;
            }

            // Check account has enough funds
            if (account.getBalance(currencyUUID) < wage) {
                notifyOwner(account, "<red>⚠ Payroll: insufficient funds to pay <white>"
                        + Bukkit.getOfflinePlayer(member.getPlayerUUID()).getName() + "<red>.");
                continue;
            }

            // Resolve payroll destination
            UUID destAccountUUID = member.getPayrollDestinationAccountUUID();
            BankAccount destAccount = destAccountUUID != null ? accounts.get(destAccountUUID) : null;

            if (destAccountUUID != null && destAccount == null) {
                // Destination account was deleted — skip and flag
                notifyOwner(account, "<red>⚠ Payroll: <white>"
                        + Bukkit.getOfflinePlayer(member.getPlayerUUID()).getName()
                        + " <red>has an invalid payroll destination. Skipping.");
                notifyPlayer(member.getPlayerUUID(),
                        "<red>⚠ Your payroll destination account no longer exists. Update it with <white>/bank<red>.");
                continue;
            }

            // Deduct from business account
            account.subtractBalance(currencyUUID, wage);
            storage.saveBalance(account.getAccountUUID(), currencyUUID, account.getBalance(currencyUUID));

            UUID actor = actorUUID != null ? actorUUID : account.getOwnerUUID();
            recordTransaction(account, BankTransaction.Type.PAYROLL_OUT, currencyUUID, wage, actor);

            if (destAccount != null) {
                // Pay into employee's chosen bank account
                destAccount.addBalance(currencyUUID, wage);
                storage.saveBalance(destAccount.getAccountUUID(), currencyUUID, destAccount.getBalance(currencyUUID));
                recordTransaction(destAccount, BankTransaction.Type.PAYROLL_IN, currencyUUID, wage, actor);
            } else {
                // Pay into employee's personal wallet
                Wallet wallet = plugin.getWalletManager().getWallets().get(member.getPlayerUUID());
                if (wallet == null) {
                    wallet = plugin.getWalletManager().newUser(member.getPlayerUUID(), true);
                }
                wallet.getCurrencyWallets()
                        .computeIfAbsent(currencyUUID, uuid -> new CurrencyWallet(uuid, 0))
                        .addAmount(wage);
                plugin.getStorageManager().saveWallet(wallet);
            }

            notifyPlayer(member.getPlayerUUID(), "<green>✔ You received a payroll payment of <white>"
                    + formatAmount(wage) + "<green> from <white>" + account.getName() + "<green>.");
        }

        payroll.setLastRunMs(System.currentTimeMillis());
        storage.savePayroll(payroll);
    }

    // ---- Tier helpers ----

    public BankTier getTier(int level) {
        return tiers.get(level);
    }

    public BankTier getNextTier(int currentLevel) {
        return tiers.get(currentLevel + 1);
    }

    public boolean hasTier(int level) {
        return tiers.containsKey(level);
    }

    public void addOrUpdateTier(BankTier tier) {
        tiers.put(tier.getLevel(), tier);
        storage.saveTier(tier);
    }

    // ---- Queries ----

    public BankAccount getAccount(UUID accountUUID) {
        return accounts.get(accountUUID);
    }

    public List<BankAccount> getAccountsForPlayer(UUID playerUUID) {
        return accounts.values().stream()
                .filter(a -> a.hasMember(playerUUID))
                .collect(Collectors.toList());
    }

    public List<BankAccount> getOwnedAccounts(UUID playerUUID) {
        return accounts.values().stream()
                .filter(a -> a.getOwnerUUID().equals(playerUUID))
                .collect(Collectors.toList());
    }

    public Map<UUID, BankAccount> getAllAccounts() {
        return accounts;
    }

    public Map<Integer, BankTier> getAllTiers() {
        return tiers;
    }

    // ---- Helpers ----

    private void notifyOwner(BankAccount account, String message) {
        notifyPlayer(account.getOwnerUUID(), message);
    }

    private void notifyPlayer(UUID playerUUID, String message) {
        Player online = Bukkit.getPlayer(playerUUID);
        if (online != null) {
            online.sendMessage(MiniMessage.miniMessage().deserialize(message));
        }
    }

    private String formatAmount(double amount) {
        return amount % 1 == 0 ? String.valueOf((long) amount) : String.valueOf(amount);
    }

    public long countAccountsWithBalance(UUID currencyUUID) {
        return accounts.values().stream()
                .filter(a -> a.getBalance(currencyUUID) > 0)
                .count();
    }

    public long countPayrollsUsing(UUID currencyUUID) {
        return accounts.values().stream()
                .filter(a -> a.isBusiness()
                        && a.getScheduledPayroll() != null
                        && currencyUUID.equals(a.getScheduledPayroll().getCurrencyUUID()))
                .count();
    }

    public BankStorage getStorage() {
        return storage;
    }
}
