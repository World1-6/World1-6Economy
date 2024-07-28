package com.andrew121410.mc.world16economy.listeners;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.user.Wallet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {

    private final World16Economy plugin;

    public OnPlayerQuitEvent(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Wallet wallet = this.plugin.getWalletManager().getWallets().get(player.getUniqueId());
        if (wallet == null) return; // Should never happen. (just in case)
        this.plugin.getStorageManager().saveWallet(wallet, true);
    }
}
