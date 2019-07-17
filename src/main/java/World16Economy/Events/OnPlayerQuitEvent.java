package World16Economy.Events;

import World16Economy.CustomExceptions.NoUserDataConfigException;
import World16Economy.Main.Main;
import World16Economy.Managers.DataManager;
import World16Economy.Objects.UserObject;
import World16Economy.TheCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerQuitEvent implements Listener {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;

    //Manager's
    private DataManager dataManager;
    private TheCore theCore;

    public OnPlayerQuitEvent(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.dataManager = this.plugin.getDataManager();
        this.theCore = this.plugin.getVaultManager().getTheCore();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws NoUserDataConfigException {
        Player p = event.getPlayer();

        if (!dataManager.saveUserObjectToConfig(p.getUniqueId())) {
            throw new NoUserDataConfigException("The User: " + p.getDisplayName() + " doesn't have a ConfigSection or is not in the HashMap UUID: " + p.getUniqueId());
        }

        moneyMap.remove(p.getUniqueId());
    }
}
