package com.andrew121410.mc.world16economy.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.SQLite;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.EasySQL;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.MultiTableEasySQL;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.SQLDataStore;
import com.google.common.collect.Multimap;

import java.sql.SQLException;
import java.util.*;

public class BankStorage {

    private final World16Economy plugin;

    private final EasySQL accounts;
    private final EasySQL balances;
    private final EasySQL members;
    private final EasySQL transactions;
    private final EasySQL tiers;
    private final EasySQL payroll;

    public BankStorage(World16Economy plugin) {
        this.plugin = plugin;

        SQLite sqlite = new SQLite(plugin.getDataFolder(), "BankAccounts");
        MultiTableEasySQL multi = new MultiTableEasySQL(sqlite);

        this.accounts = new EasySQL("Accounts", multi);
        accounts.create(List.of("AccountUUID", "Name", "OwnerUUID", "Type", "TierLevel"), false);

        this.balances = new EasySQL("Balances", multi);
        balances.create(List.of("AccountUUID", "CurrencyUUID", "Balance"), false);

        this.members = new EasySQL("Members", multi);
        members.create(List.of("AccountUUID", "PlayerUUID", "Role", "Wage", "PayrollDestination"), false);

        this.transactions = new EasySQL("Transactions", multi);
        transactions.create(List.of("TransactionUUID", "AccountUUID", "Type", "CurrencyUUID", "Amount", "ActorUUID", "Timestamp"), false);

        this.tiers = new EasySQL("Tiers", multi);
        tiers.create(List.of("TierLevel", "DisplayName", "CreationCost", "BalanceLimit", "TransactionLimit"), false);

        this.payroll = new EasySQL("Payroll", multi);
        payroll.create(List.of("AccountUUID", "CurrencyUUID", "IntervalMs", "LastRunMs", "OnlineOnly"), false);
    }

    // ---- Accounts ----

    public void saveAccount(BankAccount account) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", account.getAccountUUID().toString());
            accounts.delete(del);

            SQLDataStore store = new SQLDataStore();
            store.put("AccountUUID", account.getAccountUUID().toString());
            store.put("Name", account.getName());
            store.put("OwnerUUID", account.getOwnerUUID().toString());
            store.put("Type", account.getType().name());
            store.put("TierLevel", String.valueOf(account.getTierLevel()));
            accounts.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(UUID accountUUID) {
        try {
            SQLDataStore q = new SQLDataStore();
            q.put("AccountUUID", accountUUID.toString());
            accounts.delete(q);
            balances.delete(q);
            members.delete(q);
            transactions.delete(q);
            payroll.delete(q);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BankAccount> loadAllAccounts() {
        List<BankAccount> result = new ArrayList<>();
        try {
            Multimap<String, SQLDataStore> rows = accounts.getEverything();
            if (rows == null) return result;
            for (SQLDataStore row : rows.values()) {
                UUID accountUUID = UUID.fromString(row.get("AccountUUID"));
                String name = row.get("Name");
                UUID ownerUUID = UUID.fromString(row.get("OwnerUUID"));
                BankAccountType type = BankAccountType.valueOf(row.get("Type"));
                int tierLevel = Integer.parseInt(row.get("TierLevel"));
                result.add(new BankAccount(accountUUID, name, ownerUUID, type, tierLevel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ---- Balances ----

    public void saveBalance(UUID accountUUID, UUID currencyUUID, double balance) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", accountUUID.toString());
            del.put("CurrencyUUID", currencyUUID.toString());
            balances.delete(del);

            SQLDataStore store = new SQLDataStore();
            store.put("AccountUUID", accountUUID.toString());
            store.put("CurrencyUUID", currencyUUID.toString());
            store.put("Balance", String.valueOf(balance));
            balances.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadBalancesInto(BankAccount account) {
        try {
            SQLDataStore q = new SQLDataStore();
            q.put("AccountUUID", account.getAccountUUID().toString());
            Multimap<String, SQLDataStore> rows = balances.get(q);
            if (rows == null) return;
            for (SQLDataStore row : rows.values()) {
                UUID currencyUUID = UUID.fromString(row.get("CurrencyUUID"));
                double balance = Double.parseDouble(row.get("Balance"));
                account.setBalance(currencyUUID, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---- Members ----

    public void saveMember(UUID accountUUID, BankMember member) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", accountUUID.toString());
            del.put("PlayerUUID", member.getPlayerUUID().toString());
            members.delete(del);

            SQLDataStore store = new SQLDataStore();
            store.put("AccountUUID", accountUUID.toString());
            store.put("PlayerUUID", member.getPlayerUUID().toString());
            store.put("Role", member.getRole().name());
            store.put("Wage", String.valueOf(member.getWage()));
            store.put("PayrollDestination", member.getPayrollDestinationAccountUUID() != null
                    ? member.getPayrollDestinationAccountUUID().toString() : "");
            members.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMember(UUID accountUUID, UUID playerUUID) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", accountUUID.toString());
            del.put("PlayerUUID", playerUUID.toString());
            members.delete(del);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMembersInto(BankAccount account) {
        try {
            SQLDataStore q = new SQLDataStore();
            q.put("AccountUUID", account.getAccountUUID().toString());
            Multimap<String, SQLDataStore> rows = members.get(q);
            if (rows == null) return;
            for (SQLDataStore row : rows.values()) {
                UUID playerUUID = UUID.fromString(row.get("PlayerUUID"));
                BankRole role = BankRole.valueOf(row.get("Role"));
                double wage = Double.parseDouble(row.get("Wage"));
                String dest = row.get("PayrollDestination");
                UUID destUUID = (dest != null && !dest.isEmpty()) ? UUID.fromString(dest) : null;
                // Don't double-add owner (already added in constructor)
                account.getMembers().put(playerUUID, new BankMember(playerUUID, role, wage, destUUID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---- Transactions ----

    public void saveTransaction(BankTransaction tx) {
        try {
            SQLDataStore store = new SQLDataStore();
            store.put("TransactionUUID", tx.getTransactionUUID().toString());
            store.put("AccountUUID", tx.getAccountUUID().toString());
            store.put("Type", tx.getType().name());
            store.put("CurrencyUUID", tx.getCurrencyUUID().toString());
            store.put("Amount", String.valueOf(tx.getAmount()));
            store.put("ActorUUID", tx.getActorUUID().toString());
            store.put("Timestamp", String.valueOf(tx.getTimestamp()));
            transactions.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BankTransaction> loadTransactions(UUID accountUUID) {
        List<BankTransaction> result = new ArrayList<>();
        try {
            SQLDataStore q = new SQLDataStore();
            q.put("AccountUUID", accountUUID.toString());
            Multimap<String, SQLDataStore> rows = transactions.get(q);
            if (rows == null) return result;
            for (SQLDataStore row : rows.values()) {
                result.add(new BankTransaction(
                        UUID.fromString(row.get("TransactionUUID")),
                        UUID.fromString(row.get("AccountUUID")),
                        BankTransaction.Type.valueOf(row.get("Type")),
                        UUID.fromString(row.get("CurrencyUUID")),
                        Double.parseDouble(row.get("Amount")),
                        UUID.fromString(row.get("ActorUUID")),
                        Long.parseLong(row.get("Timestamp"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        result.sort(Comparator.comparingLong(BankTransaction::getTimestamp).reversed());
        return result;
    }

    // ---- Tiers ----

    public void saveTier(BankTier tier) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("TierLevel", String.valueOf(tier.getLevel()));
            tiers.delete(del);

            SQLDataStore store = new SQLDataStore();
            store.put("TierLevel", String.valueOf(tier.getLevel()));
            store.put("DisplayName", tier.getDisplayName());
            store.put("CreationCost", String.valueOf(tier.getCreationCost()));
            store.put("BalanceLimit", String.valueOf(tier.getBalanceLimit()));
            store.put("TransactionLimit", String.valueOf(tier.getTransactionLimit()));
            tiers.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BankTier> loadAllTiers() {
        List<BankTier> result = new ArrayList<>();
        try {
            Multimap<String, SQLDataStore> rows = tiers.getEverything();
            if (rows == null) return result;
            for (SQLDataStore row : rows.values()) {
                result.add(new BankTier(
                        Integer.parseInt(row.get("TierLevel")),
                        row.get("DisplayName"),
                        Double.parseDouble(row.get("CreationCost")),
                        Double.parseDouble(row.get("BalanceLimit")),
                        Double.parseDouble(row.get("TransactionLimit"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        result.sort(Comparator.comparingInt(BankTier::getLevel));
        return result;
    }

    // ---- Payroll ----

    public void savePayroll(BankScheduledPayroll p) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", p.getAccountUUID().toString());
            payroll.delete(del);

            SQLDataStore store = new SQLDataStore();
            store.put("AccountUUID", p.getAccountUUID().toString());
            store.put("CurrencyUUID", p.getCurrencyUUID().toString());
            store.put("IntervalMs", String.valueOf(p.getIntervalMs()));
            store.put("LastRunMs", String.valueOf(p.getLastRunMs()));
            store.put("OnlineOnly", String.valueOf(p.isOnlineOnly()));
            payroll.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayroll(UUID accountUUID) {
        try {
            SQLDataStore del = new SQLDataStore();
            del.put("AccountUUID", accountUUID.toString());
            payroll.delete(del);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPayrollInto(BankAccount account) {
        try {
            SQLDataStore q = new SQLDataStore();
            q.put("AccountUUID", account.getAccountUUID().toString());
            Multimap<String, SQLDataStore> rows = payroll.get(q);
            if (rows == null || rows.isEmpty()) return;
            SQLDataStore row = rows.values().iterator().next();
            String onlineOnlyStr = row.get("OnlineOnly");
            boolean onlineOnly = onlineOnlyStr != null && Boolean.parseBoolean(onlineOnlyStr);
            account.setScheduledPayroll(new BankScheduledPayroll(
                    account.getAccountUUID(),
                    UUID.fromString(row.get("CurrencyUUID")),
                    Long.parseLong(row.get("IntervalMs")),
                    Long.parseLong(row.get("LastRunMs")),
                    onlineOnly
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
