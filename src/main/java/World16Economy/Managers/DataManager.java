package World16Economy.Managers;

import World16Economy.Main.Main;
import World16Economy.Objects.UserObject;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.UUID;

public class DataManager {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;

    private CustomYmlManager userConfig;

    public DataManager(Main plugin, CustomConfigManager customConfigManager) {
        this.plugin = plugin;
        this.userConfig = customConfigManager.getUserData();
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
    }

    public boolean getUserObjectFromConfig(UUID uuid) {
        if (moneyMap.get(uuid) != null) {
            return true;
        }

        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) {
            this.userConfig.getConfig().createSection(uuid.toString());
            return false;
        }

        moneyMap.putIfAbsent(uuid, new UserObject(uuid, cs.getLong("balance")));
        return true;
    }

    public boolean saveUserObjectToConfig(UUID uuid) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) {
            this.userConfig.getConfig().createSection(uuid.toString());
            return false;
        }

        cs.set("balance", moneyMap.get(uuid).getBalance());
        return true;
    }

}
