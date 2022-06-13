package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.objects.UserWallet;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserWalletManager {

    private final Map<UUID, UserWallet> userWalletMap;

    private final World16Economy plugin;
    private final CustomYmlManager userConfig;

    private final API api;

    public UserWalletManager(World16Economy plugin) {
        this.plugin = plugin;
        this.api = this.plugin.getApi();

        this.userConfig = new CustomYmlManager(this.plugin, false);
        this.userConfig.setup("data.yml");
        this.userConfig.saveConfig();
        this.userConfig.reloadConfig();

        this.userWalletMap = new HashMap<>();
    }

    public UserWallet load(Player player) {
        UserWallet userWallet = getFromYML(player.getUniqueId());
        this.userWalletMap.putIfAbsent(player.getUniqueId(), userWallet);
        return userWallet;
    }

    public void save(Player player) {
        saveToYML(player.getUniqueId(), userWalletMap.get(player.getUniqueId()));
        this.userWalletMap.remove(player.getUniqueId());
    }

    public UserWallet getFromYML(UUID uuid) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        // Create new UserWallet if it doesn't exist
        if (cs == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.color(API.USELESS_TAG + " " + "New User: " + uuid));
            cs = this.userConfig.getConfig().createSection(uuid.toString());
            UserWallet userObject = new UserWallet(uuid, this.api.getDefaultMoney());
            cs.set("UserWallet", userObject);
            return userObject;
        }
        return (UserWallet) cs.get("UserWallet");
    }

    public void saveToYML(UUID uuid, UserWallet userWallet) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) cs = this.userConfig.getConfig().createSection(uuid.toString());
        cs.set("UserWallet", userWallet);
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
        return this.userWalletMap.containsKey(uuid);
    }

    public CustomYmlManager getUserConfig() {
        return userConfig;
    }

    public Map<UUID, UserWallet> getUserWalletMap() {
        return userWalletMap;
    }
}