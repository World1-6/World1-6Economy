package com.andrew121410.mc.world16economy.Managers;


import com.andrew121410.mc.world16economy.Main;

public class CustomConfigManager {

    private Main plugin;

    private CustomYmlManager userData;

    public CustomConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerAllCustomConfigs() {
        //Data.yml
        this.userData = new CustomYmlManager(this.plugin);
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
