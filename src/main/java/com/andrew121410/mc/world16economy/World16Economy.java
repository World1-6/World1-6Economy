package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.commands.bal;
import com.andrew121410.mc.world16economy.commands.eco;
import com.andrew121410.mc.world16economy.listeners.OnPlayerJoinEvent;
import com.andrew121410.mc.world16economy.listeners.OnPlayerQuitEvent;
import com.andrew121410.mc.world16economy.managers.UserWalletManager;
import com.andrew121410.mc.world16economy.managers.VaultManager;
import com.andrew121410.mc.world16economy.objects.UserWallet;
import com.andrew121410.mc.world16economy.utils.API;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class World16Economy extends JavaPlugin {

    private static World16Economy plugin;

    static {
        ConfigurationSerialization.registerClass(UserWallet.class, "UserWallet");
    }

    private API api;

    private VaultManager vaultManager;
    private UserWalletManager userWalletManager;

    public static World16Economy getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        this.api = new API(this);

        registerDefaultConfig();
        registerManagers();
        registerListeners();
        registerCommands();
    }

    public void onDisable() {
    }

    private void registerCommands() {
        new bal(this);
        new eco(this);
    }

    private void registerListeners() {
        new OnPlayerJoinEvent(this);
        new OnPlayerQuitEvent(this);
    }

    private void registerManagers() {
        this.userWalletManager = new UserWalletManager(this);
        this.vaultManager = new VaultManager(this);
    }

    private void registerDefaultConfig() {
        this.getConfig().addDefault("defaultMoney", api.getDefaultMoney());
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();

        this.api.setDefaultMoney(this.getConfig().getLong("defaultMoney"));
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public UserWalletManager getUserWalletManager() {
        return userWalletManager;
    }

    public API getApi() {
        return api;
    }
}
