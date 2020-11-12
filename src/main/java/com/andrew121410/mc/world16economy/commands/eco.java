package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.managers.DataManager;
import com.andrew121410.mc.world16economy.objects.MoneyObject;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16economy.utils.Translate;
import com.andrew121410.mc.world16economy.VaultCore;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class eco implements CommandExecutor {

    private Map<UUID, MoneyObject> moneyMap;

    private World16Economy plugin;

    //Managers
    private DataManager dataManager;

    private VaultCore vaultCore;
    private API api;

    public eco(World16Economy plugin) {
        this.plugin = plugin;
        this.moneyMap = this.plugin.getSetListMap().getMoneyMap();
        this.api = this.plugin.getApi();

        this.dataManager = this.plugin.getDataManager();
        this.vaultCore = this.plugin.getVaultManager().getVaultCore();

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
            p.sendMessage(Translate.chat("&6/eco give:take/set:reset"));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (!p.hasPermission("world16.eco.give")) {
                p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
                return true;
            }
            String playerString = args[1];
            Player target = this.plugin.getServer().getPlayer(playerString);
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

            vaultCore.depositPlayer(target.getUniqueId().toString(), (double) amount);
            p.sendMessage(Translate.chat("&a$" + amount + " has been added to " + target.getDisplayName() + " account. &9New Balance: &a$" + moneyMap.get(target.getUniqueId()).getBalance()));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("take")) {
            if (!p.hasPermission("world16.eco.take")) {
                p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
                return true;
            }
            String playerString = args[1];
            Player target = this.plugin.getServer().getPlayer(playerString);
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

            if (vaultCore.withdrawPlayer(target.getUniqueId().toString(), amount).type == EconomyResponse.ResponseType.SUCCESS) {
                p.sendMessage(Translate.chat("&e$" + amount + " &ahas been taken from " + target.getDisplayName() + " account. &9New balance: &e$" + moneyMap.get(target.getUniqueId()).getBalance()));
                return true;
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (!p.hasPermission("world16.eco.set")) {
                p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
                return true;
            }
            String playerString = args[1];
            Player target = this.plugin.getServer().getPlayer(playerString);
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

            moneyMap.get(target.getUniqueId()).setBalance(amount);
            target.sendMessage(Translate.chat("&aYour balance was set to $" + amount));
            p.sendMessage(Translate.chat("&aYou set " + target.getDisplayName() + "'s balance to $" + amount));
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (!p.hasPermission("world16.eco.reset")) {
                p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
                return true;
            }
            String playerString = args[1];
            Player target = this.plugin.getServer().getPlayer(playerString);

            if (!targetChecker(p, target)) return true;

            moneyMap.get(target.getUniqueId()).setBalance(api.getDEFAULT_MONEY());
            target.sendMessage(Translate.chat("&aYour balance was set to $" + api.getDEFAULT_MONEY()));
            p.sendMessage(Translate.chat("&aYou set " + target.getDisplayName() + "'s balance to $" + api.getDEFAULT_MONEY()));
            return true;
        } else if (args[0].equalsIgnoreCase("debug")) {
            if (!p.hasPermission("world16.eco.debug")) {
                p.sendMessage(Translate.chat("&cYou do not have permission to use this command."));
                return true;
            }
            if (args.length == 1) {
                p.sendMessage(Translate.chat("/eco debug map <List user's in memory>"));
                return true;
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("map")) {
                Set<UUID> keySet = moneyMap.keySet();
                Set<String> playerNameSet = new HashSet<>();

                //Changes UUID to playerNames
                keySet.forEach(k -> playerNameSet.add(Bukkit.getPlayer(k).getDisplayName()));
                String[] stringArray = playerNameSet.toArray(new String[0]);
                Arrays.sort(stringArray);
                String playerNames = String.join(" ", stringArray);

                String complete = "&6" + playerNames;
                p.sendMessage(Translate.chat(complete));
                return true;
            }
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
            p.sendMessage(this.getClass() + " " + "!dataManager.isUserMap" + " " + "LINE: 94");
            return false;
        }
        return true;
    }
}