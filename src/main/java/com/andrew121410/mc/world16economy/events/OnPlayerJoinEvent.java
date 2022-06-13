package com.andrew121410.mc.world16economy.events;

import com.andrew121410.mc.world16economy.World16Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {

    private final World16Economy plugin;

    public OnPlayerJoinEvent(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getUserWalletManager().load(player);
    }
}
