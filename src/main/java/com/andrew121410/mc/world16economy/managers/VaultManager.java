package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.VaultCore;
import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16utils.chat.Translate;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class VaultManager {

    private final VaultCore vaultCore;
    private final World16Economy plugin;

    public VaultManager(World16Economy plugin) {
        this.plugin = plugin;
        this.vaultCore = new VaultCore(this.plugin);

        setupEconomy();
    }

    private void setupEconomy() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.plugin.getLogger().log(java.util.logging.Level.SEVERE, "Vault was not found. Disabling plugin.");
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            return;
        }
        this.plugin.getServer().getServicesManager().register(Economy.class, vaultCore, this.plugin, ServicePriority.Low);
        this.plugin.getLogger().log(java.util.logging.Level.INFO, Translate.color("&6Vault hooked."));
    }

    public VaultCore getVaultCore() {
        return vaultCore;
    }
}
