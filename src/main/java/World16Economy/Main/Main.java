package World16Economy.Main;

import World16Economy.Managers.CustomConfigManager;
import World16Economy.Managers.DataManager;
import World16Economy.Utils.SetListMap;
import World16Economy.Managers.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Main plugin;

    private SetListMap setListMap;

    //Managers
    private CustomConfigManager customConfigManager;
    private VaultManager vaultManager;
    private DataManager dataManager;

    public void onEnable() {
        this.plugin = this;
        this.setListMap = new SetListMap();

        registerAllManagers();
    }

    public void onDisable() {
    }

    private void registerAllManagers() {
        this.customConfigManager = new CustomConfigManager(this.plugin);
        this.customConfigManager.registerAllCustomConfigs();

        this.vaultManager = new VaultManager(this.plugin);
    }

    public Main getPlugin() {
        return plugin;
    }

    public CustomConfigManager getCustomConfigManager() {
        return customConfigManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public SetListMap getSetListMap() {
        return setListMap;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
