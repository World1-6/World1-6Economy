package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.managers.UserWalletManager;
import com.andrew121410.mc.world16economy.objects.UserWallet;
import com.andrew121410.mc.world16economy.utils.API;
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

    private final Map<UUID, UserWallet> moneyMap;

    private final World16Economy plugin;
    private final UserWalletManager userWalletManager;
    private final API api;

    public VaultCore(World16Economy plugin) {
        this.plugin = plugin;
        this.api = this.plugin.getApi();
        this.userWalletManager = this.plugin.getUserWalletManager();
        this.moneyMap = this.plugin.getUserWalletManager().getUserWalletMap();
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
        return "$" + (long) amount;
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String uuid) {
        return userWalletManager.isUser(UUID.fromString(uuid));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return userWalletManager.isUserConfig(offlinePlayer.getUniqueId());
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
        return moneyMap.get(UUID.fromString(uuid)).getBalanceExact();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        UserWallet userWallet = this.moneyMap.get(offlinePlayer.getUniqueId());
        return userWallet != null ? userWallet.getBalanceExact() : 0;
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
        UUID realUUID = UUID.fromString(uuid);
        Player target = Bukkit.getPlayer(realUUID);

        if (target != null) {
            if (userWalletManager.isUser(UUID.fromString(uuid))) {
                return moneyMap.get(realUUID).hasEnough((long) amount);
            }
        }
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        if (userWalletManager.isUserConfig(offlinePlayer.getUniqueId())) {
            UserWallet userWallet = this.moneyMap.get(offlinePlayer.getUniqueId());
            return userWallet.hasEnough((long) amount);
        }
        return false;
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
        if (player != null) {
            if (userWalletManager.isUser(UUID.fromString(uuid))) {
                if (this.moneyMap.get(UUID.fromString(uuid)).hasEnough((long) amount)) {
                    this.moneyMap.get(UUID.fromString(uuid)).subtractBalance((long) amount);
                    player.sendMessage(Translate.color("&e$" + (long) amount + " &ahas been taken from your account."));
                    return new EconomyResponse(amount, this.moneyMap.get(UUID.fromString(uuid)).getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
                } else {
                    player.sendMessage(Translate.color("You do not have enough money dumper."));
                    return new EconomyResponse(amount, this.moneyMap.get(UUID.fromString(uuid)).getBalanceExact(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
                }
            } else {
                player.sendMessage(Translate.color("You do not have an account?"));
                return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "You do not have an account!");
            }
        }
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Not a valid player?");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (userWalletManager.isUserConfig(offlinePlayer.getUniqueId())) {
            UserWallet userWallet = this.moneyMap.get(offlinePlayer.getUniqueId());
            if (userWallet.hasEnough((long) amount)) {
                userWallet.subtractBalance((long) amount);
                userWalletManager.saveToYML(offlinePlayer.getUniqueId(), userWallet);
                return new EconomyResponse(amount, userWallet.getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
            } else {
                return new EconomyResponse(amount, userWallet.getBalanceExact(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
            }
        }
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "You do not have an account!");
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

        if (player != null) {
            if (userWalletManager.isUser(UUID.fromString(uuid))) {
                moneyMap.get(UUID.fromString(uuid)).addBalance((long) amount);
                player.sendMessage(Translate.color("&a$" + (long) amount + " has been added to your account."));
                return new EconomyResponse(amount, moneyMap.get(UUID.fromString(uuid)).getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
            } else {
                return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player does not have an account!");
            }
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Not a player?");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (userWalletManager.isUserConfig(offlinePlayer.getUniqueId())) {
            UserWallet userWallet = this.userWalletManager.getFromYML(offlinePlayer.getUniqueId());
            userWallet.addBalance((long) amount);
            this.userWalletManager.saveToYML(offlinePlayer.getUniqueId(), userWallet);
            return new EconomyResponse(amount, moneyMap.get(offlinePlayer.getUniqueId()).getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
        }
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player does not have an account!");
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
        if (!hasAccount(uuid)) {
            userWalletManager.getFromYML(UUID.fromString(uuid));
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (!hasAccount(offlinePlayer)) {
            UserWallet userWallet = new UserWallet(offlinePlayer.getUniqueId(), this.api.getDefaultMoney());
            this.userWalletManager.saveToYML(offlinePlayer.getUniqueId(), userWallet);
            return true;
        }
        return false;
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