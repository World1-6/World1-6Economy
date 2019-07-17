package World16Economy.Commands;

import World16Economy.Main.Main;
import World16Economy.Managers.DataManager;
import World16Economy.Objects.UserObject;
import World16Economy.TheCore;
import World16Economy.Utils.API;
import World16Economy.Utils.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class eco implements CommandExecutor {

    private Map<UUID, UserObject> moneyMap;

    private Main plugin;

    //Managers
    private DataManager dataManager;

    private TheCore theCore;

    public eco(Main plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();

        this.dataManager = this.plugin.getDataManager();
        this.theCore = this.plugin.getVaultManager().getTheCore();

        this.plugin.getCommand("eco").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("world16.eco")) {
            p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Translate.chat(""));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            String playerString = args[1];
            Player target = this.plugin.getServer().getPlayerExact(playerString);
            long amount = 0;

            if (!API.isLong(args[2])) {
                p.sendMessage(Translate.chat("Not a valid long(int)"));
                return true;
            }

            amount = Long.parseLong(args[2]);

            if (!targetChecker(p, target)) return true;

            if (amount == 0) {
                p.sendMessage(Translate.chat("The amount can't be 0?"));
                return true;
            }

            theCore.depositPlayer(target.getUniqueId().toString(), (double) amount);
            p.sendMessage(Translate.chat("DONE"));
            return true;
        }
        return true;
    }

    private Boolean targetChecker(Player p, Player targetPlayer) {
        if (targetPlayer == null) {
            p.sendMessage(Translate.chat("I'm a 100% sure that isn't a player?"));
            return false;
        }

        if (!targetPlayer.isOnline()) {
            p.sendMessage(Translate.chat("That Player isn't online?"));
            return false;
        }

        if (!dataManager.isUserMap(targetPlayer.getUniqueId())) {
            p.sendMessage(Translate.chat("&cIf you see this report this to Andrew121410#2035 on discord&r"));
            p.sendMessage(this.getClass() + " " + "!dataManager.isUserMap" + " " + "LINE: 73");
            return false;
        }
        return true;
    }
}