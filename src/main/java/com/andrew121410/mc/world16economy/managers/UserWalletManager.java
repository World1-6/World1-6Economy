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
        return this.load(player.getUniqueId());
    }

    public UserWallet load(UUID uuid) {
        UserWallet userWallet = getFromYML(uuid, true);
        this.userWalletMap.putIfAbsent(uuid, userWallet);
        return userWallet;
    }

    public void save(Player player) {
        saveToYML(player.getUniqueId(), userWalletMap.get(player.getUniqueId()));
        this.userWalletMap.remove(player.getUniqueId());
    }

    public UserWallet getFromYML(UUID uuid, boolean createIfNotExist) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (createIfNotExist && cs == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.color(API.USELESS_TAG + " " + "New User: " + uuid));
            UserWallet userWallet = new UserWallet(uuid, api.getDefaultMoney());
            saveToYML(uuid, userWallet);
            return userWallet;
        }

        UserWallet userWallet;
        try {
            userWallet = (UserWallet) cs.get("UserWallet");
        } catch (Exception e) {
            userWallet = null;
        }
        return userWallet;
    }

    public void saveToYML(UUID uuid, UserWallet userWallet) {
        ConfigurationSection cs = this.userConfig.getConfig().getConfigurationSection(uuid.toString());
        if (cs == null) cs = this.userConfig.getConfig().createSection(uuid.toString());
        cs.set("UserWallet", userWallet);
        this.userConfig.saveConfig();
    }

    public boolean isUser(UUID uuid) {
        return isInMemory(uuid) || getFromYML(uuid, false) != null;
    }

    public boolean isInMemory(UUID uuid) {
        return this.userWalletMap.containsKey(uuid);
    }

    public CustomYmlManager getUserConfig() {
        return userConfig;
    }

    public Map<UUID, UserWallet> getUserWalletMap() {
        return userWalletMap;
    }
}