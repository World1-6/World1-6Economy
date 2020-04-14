package com.andrew121410.mc.world16economy.Events;

import com.andrew121410.mc.world16economy.Main;
import com.andrew121410.mc.world16economy.Managers.DataManager;
import com.andrew121410.mc.world16economy.Objects.MoneyObject;
import com.andrew121410.mc.world16economy.Utils.API;
import com.andrew121410.mc.world16economy.Utils.Translate;
import com.andrew121410.mc.world16economy.VaultCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerJoinEvent implements Listener {

    private Map<UUID, MoneyObject> moneyMap;

    private Main plugin;
    private API api;

    //Manager's
    private DataManager dataManager;
    private VaultCore vaultCore;

    public OnPlayerJoinEvent(Main plugin) {
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

        if (api.isDEBUG()) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.DEBUG_TAG + " User: " + p.getUniqueId() + " has been ADDED to memory since they joined the server."));
        }

        dataManager.load(p);
    }
}
