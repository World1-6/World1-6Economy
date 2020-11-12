package com.andrew121410.mc.world16economy.events;

import com.andrew121410.mc.world16economy.VaultCore;
import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.managers.DataManager;
import com.andrew121410.mc.world16economy.objects.MoneyObject;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerJoinEvent implements Listener {

    private Map<UUID, MoneyObject> moneyMap;

    private World16Economy plugin;
    private API api;

    //Manager's
    private DataManager dataManager;
    private VaultCore vaultCore;

    public OnPlayerJoinEvent(World16Economy plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.dataManager = this.plugin.getDataManager();
        this.vaultCore = this.plugin.getVaultManager().getVaultCore();
        this.api = this.plugin.getApi();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (api.isDebug()) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.color(API.DEBUG_TAG + " User: " + p.getUniqueId() + " has been ADDED to memory since they joined the server."));
        }

        dataManager.load(p);
    }
}
