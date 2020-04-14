package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.Main;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16economy.utils.Translate;
import com.andrew121410.mc.world16economy.VaultCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class VaultManager {

    private VaultCore vaultCore;
    private Main plugin;

    public VaultManager(Main plugin) {
        this.plugin = plugin;
        this.vaultCore = new VaultCore(this.plugin);

        setupEconomy();
    }

    private void setupEconomy() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.EMERGENCY_TAG + " " + "&cVault was not found?"));
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
        }
        this.plugin.getServer().getServicesManager().register(Economy.class, vaultCore, this.plugin, ServicePriority.Low);
        this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.USELESS_TAG + " " + "&aVault was found."));
    }

    public VaultCore getVaultCore() {
        return vaultCore;
    }

    public Main getPlugin() {
        return plugin;
    }
}
