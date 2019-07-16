package World16Economy.Events;

import World16Economy.Main.Main;
import World16Economy.Managers.DataManager;
import World16Economy.Objects.UserObject;
import World16Economy.TheCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerJoinEvent implements Listener {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;

    //Manager's
    private DataManager dataManager;
    private TheCore theCore;

    public OnPlayerJoinEvent(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.dataManager = this.plugin.getDataManager();
        this.theCore = this.plugin.getVaultManager().getTheCore();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        dataManager.getUserObjectFromConfig(p.getUniqueId());
    }
}
