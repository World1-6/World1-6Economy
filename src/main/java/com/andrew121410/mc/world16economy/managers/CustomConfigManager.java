package com.andrew121410.mc.world16economy.managers;


import com.andrew121410.mc.world16economy.Main;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;

public class CustomConfigManager {

    private Main plugin;

    private CustomYmlManager userData;

    public CustomConfigManager(Main plugin) {
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

    public Main getPlugin() {
        return plugin;
    }

    public CustomYmlManager getUserData() {
        return userData;
    }
}
