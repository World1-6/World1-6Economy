package com.andrew121410.mc.world16economy.events;

import com.andrew121410.mc.world16economy.Main;
import com.andrew121410.mc.world16economy.managers.DataManager;
import com.andrew121410.mc.world16economy.objects.MoneyObject;
import com.andrew121410.mc.world16economy.utils.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerQuitEvent implements Listener {

    private Map<UUID, MoneyObject> moneyMap;

    private Main plugin;
    private API api;

    //Manager's
    private DataManager dataManager;

    public OnPlayerQuitEvent(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.dataManager = this.plugin.getDataManager();
        this.api = this.plugin.getApi();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.dataManager.save(player);
        this.moneyMap.remove(player.getUniqueId());
    }
}
