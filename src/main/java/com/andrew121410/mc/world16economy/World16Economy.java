package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.commands.bal;
import com.andrew121410.mc.world16economy.commands.eco;
import com.andrew121410.mc.world16economy.listeners.OnPlayerJoinEvent;
import com.andrew121410.mc.world16economy.listeners.OnPlayerQuitEvent;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16utils.updater.UpdateManager;
import org.bukkit.plugin.java.JavaPlugin;

public class World16Economy extends JavaPlugin {

    private static World16Economy plugin;

    private CurrenciesManager currenciesManager;
    private WalletManager walletManager;

    public static World16Economy getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;

        registerManagers();
        registerListeners();
        registerCommands();

        UpdateManager.registerUpdater(this, new com.andrew121410.mc.world16economy.Updater(this));
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
        this.currenciesManager = new CurrenciesManager(this);
        this.walletManager = new WalletManager(this);

        // Last is Vault
        new VaultCore(this);
    }


    public CurrenciesManager getCurrenciesManager() {
        return currenciesManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }
}
