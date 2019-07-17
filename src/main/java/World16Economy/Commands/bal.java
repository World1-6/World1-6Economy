package World16Economy.Commands;

import World16Economy.Main.Main;
import World16Economy.Objects.UserObject;
import World16Economy.TheCore;
import World16Economy.Utils.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class bal implements CommandExecutor {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;
    private TheCore theCore;

    public bal(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.theCore = this.plugin.getVaultManager().getTheCore();

        this.plugin.getCommand("bal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("world16.bal")) {
            p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            if (moneyMap.get(p.getUniqueId()) != null) {
                p.sendMessage(Translate.chat("&aBalance:&c " + moneyMap.get(p.getUniqueId()).getBalanceFancy()));
                return true;
            } else {
                theCore.hasAccount(p.getUniqueId().toString());
                return true;
            }
        }
        return true;
    }
}
