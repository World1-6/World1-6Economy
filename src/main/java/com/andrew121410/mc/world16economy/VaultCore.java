package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// http://milkbowl.github.io/VaultAPI/
public class VaultCore implements Economy {

    private final Map<UUID, Wallet> userWalletMap;

    private final World16Economy plugin;

    private CurrenciesManager currenciesManager;
    private final WalletManager walletManager;

    public VaultCore(World16Economy plugin) {
        this.plugin = plugin;

        this.currenciesManager = this.plugin.getCurrenciesManager();
        this.walletManager = this.plugin.getWalletManager();

        this.userWalletMap = this.walletManager.getWallets();
    }

    private CurrencyWallet getCurrencyWallet(UUID playerUUID) {
        Currency currency = this.currenciesManager.getDefaultCurrency();

        Wallet wallet = this.userWalletMap.get(playerUUID);
        if (wallet == null) return null;

        return wallet.getCurrencyWallet(currency.getUuid());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "World1-6Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        Currency currency = this.currenciesManager.getDefaultCurrency();
        return currency.getSymbol() + amount;
    }

    @Override
    public String currencyNamePlural() {
        Currency currency = this.currenciesManager.getDefaultCurrency();
        return currency.getCurrencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        Currency currency = this.currenciesManager.getDefaultCurrency();
        return currency.getCurrencyNameSingular();
    }

    @Override
    public boolean hasAccount(String uuid) {
        return getCurrencyWallet(UUID.fromString(uuid)) != null;

    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(String uuid, String world) {
        return hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String uuid) {
        CurrencyWallet currencyWallet = getCurrencyWallet(UUID.fromString(uuid));

        if (currencyWallet == null) return 0.0;

        return currencyWallet.getAmount();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public double getBalance(String uuid, String worldName) {
        return getBalance(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String uuid, double amount) {
        CurrencyWallet currencyWallet = getCurrencyWallet(UUID.fromString(uuid));
        if (currencyWallet == null) return false;

        return currencyWallet.hasRequiredAmount(amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return has(offlinePlayer.getUniqueId().toString(), amount);
    }

    @Override
    public boolean has(String uuid, String worldName, double amount) {
        return has(uuid, amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String uuid, double amount) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));

        CurrencyWallet currencyWallet = getCurrencyWallet(UUID.fromString(uuid));
        if (currencyWallet == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player doesn't have an account.");
        }

        if (currencyWallet.hasRequiredAmount(amount)) {
            currencyWallet.subtractAmount(amount);

            if (player != null) {
                String symbol = this.currenciesManager.getDefaultCurrency().getSymbol();
                player.sendMessage(Translate.miniMessage("<gold>You paid <green>" + symbol + amount + "<gold>."));
            }

            return new EconomyResponse(amount, currencyWallet.getAmount(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
        } else {
            if (player != null) {
                player.sendMessage(Translate.miniMessage("<red>You do not have enough money!"));
            }
            return new EconomyResponse(amount, currencyWallet.getAmount(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return withdrawPlayer(offlinePlayer.getUniqueId().toString(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String uuid, String worldName, double amount) {
        return withdrawPlayer(uuid, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String WorldName, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String uuid, double amount) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));

        CurrencyWallet currencyWallet = getCurrencyWallet(UUID.fromString(uuid));
        if (currencyWallet == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player doesn't have an account.");
        }

        currencyWallet.addAmount(amount);

        if (player != null) {
            String symbol = this.currenciesManager.getDefaultCurrency().getSymbol();
            player.sendMessage(Translate.miniMessage("<gold>You received <green>" + symbol + amount + "<gold>."));
        }

        return new EconomyResponse(amount, currencyWallet.getAmount(), EconomyResponse.ResponseType.SUCCESS, "You received $" + amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        return depositPlayer(offlinePlayer.getUniqueId().toString(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String uuid, String worldName, double amount) {
        return depositPlayer(uuid, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String uuid) {
        if (getCurrencyWallet(UUID.fromString(uuid)) != null) return true; // Player already has an account

        Currency defaultCurrency = this.currenciesManager.getDefaultCurrency();
        CurrencyWallet currencyWallet = new CurrencyWallet(UUID.fromString(uuid), defaultCurrency.getDefaultMoney());

        Wallet wallet = this.walletManager.getWallets().get(UUID.fromString(uuid));
        if (wallet == null) {
            wallet = new Wallet(UUID.fromString(uuid));
            this.walletManager.getWallets().put(UUID.fromString(uuid), wallet);
        }

        wallet.getCurrencyWallets().put(defaultCurrency.getUuid(), currencyWallet);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public boolean createPlayerAccount(String uuid, String worldName) {
        return createPlayerAccount(uuid);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String worldName) {
        return createPlayerAccount(offlinePlayer);
    }
}