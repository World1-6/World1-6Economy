package com.andrew121410.mc.world16economy.managers;


import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;

public class CustomConfigManager {

    private World16Economy plugin;

    private CustomYmlManager userData;

    public CustomConfigManager(World16Economy plugin) {
        this.plugin = plugin;
    }

    public void registerAllCustomConfigs() {
        //Data.yml
        this.userData = new CustomYmlManager(this.plugin, this.plugin.getApi().isDEBUG());
        this.userData.setup("data.yml");
        this.userData.saveConfig();
        this.userData.reloadConfig();
        //...
    }

    public World16Economy getPlugin() {
        return plugin;
    }

    public CustomYmlManager getUserData() {
        return userData;
    }
}
