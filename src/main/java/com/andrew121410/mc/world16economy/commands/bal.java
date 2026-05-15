package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.gui.WalletListGUI;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bal implements CommandExecutor {

    private final World16Economy plugin;

    public bal(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("bal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            if (!player.hasPermission("world16.bal")) {
                player.sendMessage(Translate.miniMessage("<red>You do not have permission to use this command."));
                return true;
            }
            WalletListGUI.open(plugin, player, player);
            return true;
        }

        if (args.length == 1) {
            if (!player.hasPermission("world16.bal.other")) {
                player.sendMessage(Translate.miniMessage("<red>You do not have permission to use this command."));
                return true;
            }
            OfflinePlayer target = plugin.getServer().getOfflinePlayerIfCached(args[0]);
            if (target == null) {
                player.sendMessage(Translate.miniMessage("<red>That player does not exist."));
                return true;
            }
            WalletListGUI.open(plugin, target, player);
            return true;
        }

        return true;
    }
}
