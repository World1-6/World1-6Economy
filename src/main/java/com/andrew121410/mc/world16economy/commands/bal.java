package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bal implements CommandExecutor {

    private final World16Economy plugin;

    private WalletManager walletManager;

    public bal(World16Economy plugin) {
        this.plugin = plugin;

        this.walletManager = this.plugin.getWalletManager();

        this.plugin.getCommand("bal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        if (!player.hasPermission("world16.bal")) {
            player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            if (this.userWalletMap.containsKey(player.getUniqueId())) {
                player.sendMessage(Translate.color("&aBalance:&c " + userWalletMap.get(player.getUniqueId()).getBalanceFancy()));
            } else {
                vaultCore.hasAccount(player.getUniqueId().toString());
            }
            return true;
        } else if (args.length == 1) {
            if (!player.hasPermission("world16.bal.other")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[0]);

            if (!targetChecker(player, target)) return true;

            player.sendMessage(Translate.color("&aBalance of " + target.getDisplayName() + " is " + userWalletMap.get(target.getUniqueId()).getBalanceFancy()));
        }
        return true;
    }

    private Boolean targetChecker(Player player, Player targetPlayer) {
        if (targetPlayer == null) {
            player.sendMessage(Translate.color("&cThat isn't a valid player."));
            return false;
        }
        if (!targetPlayer.isOnline()) {
            player.sendMessage(Translate.color("7cLooks like the player isn't online."));
            return false;
        }
        if (!userWalletManager.isInMemory(targetPlayer.getUniqueId())) {
            throw new NullPointerException("User isn't in memory?");
        }
        return true;
    }
}
