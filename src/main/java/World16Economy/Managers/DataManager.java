package World16Economy.Managers;

import World16Economy.Main.Main;
import World16Economy.Objects.UserObject;
import World16Economy.Utils.API;
import World16Economy.Utils.Translate;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.UUID;

public class DataManager {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;
    private API api;

    private CustomYmlManager userConfig;

    public DataManager(Main plugin, CustomConfigManager customConfigManager) {
        this.plugin = plugin;
        this.userConfig = customConfigManager.getUserData();
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.api = this.plugin.getApi();
    }

    public boolean getUserObjectFromConfig(UUID uuid) {
        if (moneyMap.get(uuid) != null) {
            return true;
        }

        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());

        //Create new User.
        if (cs == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.USELESS_TAG + " " + "New User: " + uuid.toString()));
            this.userConfig.getConfig().createSection(uuid.toString());
            cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
            cs.set("balance", api.getDEFAULT_MONEY());
            moneyMap.putIfAbsent(uuid, new UserObject(uuid, api.getDEFAULT_MONEY()));
            return true;
        }

        moneyMap.putIfAbsent(uuid, new UserObject(uuid, cs.getLong("balance")));
        return true;
    }

    public boolean saveUserObjectToConfig(UUID uuid) {
        if (moneyMap.get(uuid) == null) {
            return false;
        }

        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) {
            this.userConfig.getConfig().createSection(uuid.toString());
            return false;
        }

        cs.set("balance", moneyMap.get(uuid).getBalanceExact());
        this.userConfig.saveConfigSilent();
        return true;
    }

    public boolean isUser(UUID uuid) {
        return isUserConfig(uuid) && isUserMap(uuid);
    }

    public boolean isUserConfig(UUID uuid) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        return cs != null;
    }

    public boolean isUserMap(UUID uuid) {
        return moneyMap.get(uuid) != null;
    }


}
