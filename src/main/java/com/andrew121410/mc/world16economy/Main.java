package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.Commands.bal;
import com.andrew121410.mc.world16economy.Commands.eco;
import com.andrew121410.mc.world16economy.Events.OnPlayerJoinEvent;
import com.andrew121410.mc.world16economy.Events.OnPlayerQuitEvent;
import com.andrew121410.mc.world16economy.Managers.CustomConfigManager;
import com.andrew121410.mc.world16economy.Managers.DataManager;
import com.andrew121410.mc.world16economy.Managers.VaultManager;
import com.andrew121410.mc.world16economy.Objects.MoneyObject;
import com.andrew121410.mc.world16economy.Utils.API;
import com.andrew121410.mc.world16economy.Utils.SetListMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(MoneyObject.class, "UserObject");
    }

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
        registerDefaultConfig();
        registerEvents();
        registerCommands();
    }

    public void onDisable() {
    }

    private void registerCommands() {
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

    private void registerDefaultConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();

        if (this.getConfig().get("defaultMoney") == null) {
            this.getConfig().set("defaultMoney", api.getDEFAULT_MONEY());
            this.saveConfig();
        }

        if (this.getConfig().get("debug") == null) {
            this.getConfig().set("debug", api.isDEBUG());
            this.saveConfig();
        }

        this.api.setDEFAULT_MONEY(this.getConfig().getLong("defaultMoney"));
        this.api.setDEBUG(this.getConfig().getBoolean("debug"));
    }

    //Getter's
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
