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

    private final Map<UUID, UserWallet> userWalletMap;

    private final World16Economy plugin;
    private final UserWalletManager userWalletManager;
    private final API api;

    public VaultCore(World16Economy plugin) {
        this.plugin = plugin;
        this.api = this.plugin.getApi();
        this.userWalletManager = this.plugin.getUserWalletManager();
        this.userWalletMap = this.plugin.getUserWalletManager().getUserWalletMap();
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
        return userWalletManager.isUser(offlinePlayer.getUniqueId());
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
        return userWalletMap.get(UUID.fromString(uuid)).getBalanceExact();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        UserWallet userWallet = this.userWalletManager.getFromYML(offlinePlayer.getUniqueId(), false);
        if (userWallet == null) return 0;
        return userWallet.getBalanceExact();
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

        UserWallet userWallet = this.userWalletMap.getOrDefault(realUUID, null);
        if (userWallet == null) return false;

        return userWallet.hasEnough((long) amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        UserWallet userWallet = this.userWalletManager.getFromYML(offlinePlayer.getUniqueId(), false);
        if (userWallet == null) return false;

        return userWallet.hasEnough((long) amount);
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

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not online?");
        }

        UserWallet userWallet = this.userWalletMap.getOrDefault(player.getUniqueId(), null);

        if (userWallet == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not registered?");
        }

        if (userWallet.hasEnough((long) amount)) {
            userWallet.subtractBalance((long) amount);
            player.sendMessage(Translate.color("&e$" + (long) amount + " &ahas been taken from your account."));
            return new EconomyResponse(amount, userWallet.getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
        } else {
            player.sendMessage(Translate.color("You do not have enough money."));
            return new EconomyResponse(amount, userWallet.getBalanceExact(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        UserWallet offlineUserWallet = this.userWalletManager.getFromYML(offlinePlayer.getUniqueId(), false);

        if (offlineUserWallet == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player doesn't have an account.");
        }

        if (offlineUserWallet.hasEnough((long) amount)) {
            offlineUserWallet.subtractBalance((long) amount);
            userWalletManager.saveToYML(offlinePlayer.getUniqueId(), offlineUserWallet);
            return new EconomyResponse(amount, offlineUserWallet.getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
        } else {
            return new EconomyResponse(amount, offlineUserWallet.getBalanceExact(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
        }
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

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not online?");
        }

        UserWallet userWallet = this.userWalletMap.getOrDefault(player.getUniqueId(), null);

        if (userWallet == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not registered?");
        }

        userWallet.addBalance((long) amount);
        player.sendMessage(Translate.color("&a$" + (long) amount + " has been added to your account."));
        return new EconomyResponse(amount, userWallet.getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        UserWallet offlineUserWallet = this.userWalletManager.getFromYML(offlinePlayer.getUniqueId(), false);

        if (offlineUserWallet == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player doesn't have an account.");
        }

        offlineUserWallet.addBalance((long) amount);
        this.userWalletManager.saveToYML(offlinePlayer.getUniqueId(), offlineUserWallet);
        return new EconomyResponse(amount, offlineUserWallet.getBalanceExact(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
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
            this.userWalletManager.load(UUID.fromString(uuid));
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (!hasAccount(offlinePlayer)) {
            this.userWalletManager.saveToYML(offlinePlayer.getUniqueId(), new UserWallet(offlinePlayer.getUniqueId(), api.getDefaultMoney()));
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