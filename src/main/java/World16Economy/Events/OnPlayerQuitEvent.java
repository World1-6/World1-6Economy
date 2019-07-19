package World16Economy.Events;

import World16Economy.CustomExceptions.NoUserDataConfigException;
import World16Economy.Main.Main;
import World16Economy.Managers.DataManager;
import World16Economy.Objects.UserObject;
import World16Economy.TheCore;
import World16Economy.Utils.API;
import World16Economy.Utils.Translate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class OnPlayerQuitEvent implements Listener {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;
    private API api;

    //Manager's
    private DataManager dataManager;
    private TheCore theCore;

    public OnPlayerQuitEvent(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.dataManager = this.plugin.getDataManager();
        this.theCore = this.plugin.getVaultManager().getTheCore();
        this.api = this.plugin.getApi();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws NoUserDataConfigException {
        Player p = event.getPlayer();

        if (!dataManager.saveUserObjectToConfig(p.getUniqueId())) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.EMERGENCY_TAG + " &cUser isn't in the HashMap either: A. You reloaded the server. B. You reloaded the plugin with some type of Plugin Manager."));
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.EMERGENCY_TAG + " &eUser: " + p.getDisplayName() + " UUID: " + p.getUniqueId()));
        } else {
            if (api.isDEBUG()) {
                this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.DEBUG_TAG + " User: " + p.getUniqueId() + " The memory for that use just been WIPED since they left the server."));
            }
            moneyMap.remove(p.getUniqueId());
        }


    }
}
