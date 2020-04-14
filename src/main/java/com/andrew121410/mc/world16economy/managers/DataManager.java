package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.Main;
import com.andrew121410.mc.world16economy.objects.MoneyObject;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16economy.utils.Translate;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class DataManager {

    private Map<UUID, MoneyObject> moneyMap;

    private Main plugin;
    private CustomYmlManager userConfig;

    private API api;

    public DataManager(Main plugin, CustomConfigManager customConfigManager) {
        this.plugin = plugin;
        this.api = this.plugin.getApi();
        this.userConfig = customConfigManager.getUserData();
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
    }

    public void load(Player player) {
        MoneyObject moneyObject = get(player.getUniqueId());
        this.moneyMap.putIfAbsent(player.getUniqueId(), moneyObject);
    }

    public void save(Player player) {
        save(player.getUniqueId(), moneyMap.get(player.getUniqueId()));
        this.moneyMap.remove(player.getUniqueId());
    }

    public MoneyObject get(UUID uuid) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());

        //Create new User.
        if (cs == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.USELESS_TAG + " " + "New User: " + uuid.toString()));
            cs = this.userConfig.getConfig().createSection(uuid.toString());
            MoneyObject userObject = new MoneyObject(uuid, this.api.getDEFAULT_MONEY());
            cs.set("MoneyObject", userObject);
            return userObject;
        }

        return (MoneyObject) cs.get("MoneyObject");
    }

    public void save(UUID uuid, MoneyObject moneyObject) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) cs = this.userConfig.getConfig().createSection(uuid.toString());
        cs.set("MoneyObject", moneyObject);
        this.userConfig.saveConfig();
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