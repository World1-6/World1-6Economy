package World16Economy;

import World16Economy.Main.Main;
import World16Economy.Managers.DataManager;
import World16Economy.Objects.UserObject;
import World16Economy.Utils.Translate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TheCore implements Economy {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;

    //Managers
    private DataManager dataManager;

    public TheCore(Main plugin) {
        this.plugin = plugin;
        this.dataManager = this.plugin.getDataManager();
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
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
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String uuid) {
        return dataManager.isUser(UUID.fromString(uuid));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return dataManager.isUser(offlinePlayer.getUniqueId());
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
        return dataManager.getUserObjectFromConfig(UUID.fromString(uuid)) ? moneyMap.get(UUID.fromString(uuid)).getBalance() : 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return dataManager.getUserObjectFromConfig(offlinePlayer.getUniqueId()) ? moneyMap.get(offlinePlayer.getUniqueId()).getBalance() : 0;
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
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return false;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String uuid, double amount) {
        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        if (p != null) {
            if (hasAccount(uuid)) {
                if (this.moneyMap.get(UUID.fromString(uuid)).checkIfHaveEnough((long) amount)) {
                    this.moneyMap.get(UUID.fromString(uuid)).subtractBalance((long) amount);
                    return new EconomyResponse(amount, this.moneyMap.get(UUID.fromString(uuid)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
                }
                p.sendMessage(Translate.chat("You do not have enough money dumper."));
                return new EconomyResponse(amount, this.moneyMap.get(UUID.fromString(uuid)).getBalance(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
            }
            p.sendMessage(Translate.chat("You do not have an account?"));
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "You do not have an account!");
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (hasAccount(offlinePlayer.getUniqueId().toString())) {
            if (this.moneyMap.get(offlinePlayer.getUniqueId()).checkIfHaveEnough((long) amount)) {
                this.moneyMap.get(offlinePlayer.getUniqueId()).subtractBalance((long) amount);
                return new EconomyResponse(amount, this.moneyMap.get(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "You paid $" + amount);
            }
            return new EconomyResponse(amount, this.moneyMap.get(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.FAILURE, "You do not have enough money!");
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
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            if (hasAccount(uuid)) {
                moneyMap.get(UUID.fromString(uuid)).addBalance((long) amount);
                p.sendMessage("You have been paid " + amount);
                return new EconomyResponse(amount, moneyMap.get(UUID.fromString(uuid)).getBalance(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
            } else {
                return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player does not have an account!");
            }
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (hasAccount(offlinePlayer)) {
            moneyMap.get(offlinePlayer.getUniqueId()).addBalance((long) amount);
            return new EconomyResponse(amount, moneyMap.get(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "You have been paid $" + amount);
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
            dataManager.getUserObjectFromConfig(UUID.fromString(uuid));
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (!hasAccount(String.valueOf(offlinePlayer.getUniqueId()))) {
            dataManager.getUserObjectFromConfig(offlinePlayer.getUniqueId());
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
