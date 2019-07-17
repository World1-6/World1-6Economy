package World16Economy.Managers;

import World16Economy.Main.Main;
import World16Economy.TheCore;
import World16Economy.Utils.API;
import World16Economy.Utils.Translate;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class VaultManager {

    private TheCore theCore;
    private Main plugin;

    public VaultManager(Main plugin) {
        this.plugin = plugin;
        this.theCore = new TheCore(this.plugin);

        setupEconomy();
    }

    private void setupEconomy() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.EMERGENCY_TAG + " " + "&cVault was not found?"));
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
        }
        this.plugin.getServer().getServicesManager().register(Economy.class, theCore, this.plugin, ServicePriority.Low);
        this.plugin.getServer().getConsoleSender().sendMessage(Translate.chat(API.USELESS_TAG + " " + "&aVault was found."));
    }

    public TheCore getTheCore() {
        return theCore;
    }

    public Main getPlugin() {
        return plugin;
    }
}
