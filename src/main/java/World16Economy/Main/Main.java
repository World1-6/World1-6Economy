package World16Economy.Main;

import World16Economy.Commands.bal;
import World16Economy.Commands.eco;
import World16Economy.Events.OnPlayerJoinEvent;
import World16Economy.Events.OnPlayerQuitEvent;
import World16Economy.Managers.CustomConfigManager;
import World16Economy.Managers.DataManager;
import World16Economy.Managers.VaultManager;
import World16Economy.Utils.API;
import World16Economy.Utils.SetListMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Main plugin;

    private SetListMap setListMap;
    private API api;

    //Managers
    private CustomConfigManager customConfigManager;
    private VaultManager vaultManager;
    private DataManager dataManager;

    public void onEnable() {
        this.plugin = this;
        this.setListMap = new SetListMap();
        this.api = new API(this);

        registerAllManagers();
        registerEvents();
        registerCommands();
    }

    public void onDisable() {
    }

    public void registerCommands() {
        new bal(this);
        new eco(this);
    }

    private void registerEvents() {
        new OnPlayerJoinEvent(this);
        new OnPlayerQuitEvent(this);
    }

    private void registerAllManagers() {
        this.customConfigManager = new CustomConfigManager(this);
        this.customConfigManager.registerAllCustomConfigs();

        this.dataManager = new DataManager(this, this.customConfigManager);

        this.vaultManager = new VaultManager(this);
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

    public API getApi() {
        return api;
    }
}
