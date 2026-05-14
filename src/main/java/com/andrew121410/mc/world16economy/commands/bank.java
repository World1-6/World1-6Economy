package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.gui.bank.BankAccountGUI;
import com.andrew121410.mc.world16economy.gui.bank.BankListGUI;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class bank implements CommandExecutor, TabCompleter {

    private final World16Economy plugin;

    public bank(World16Economy plugin) {
        this.plugin = plugin;
        plugin.getCommand("bank").setExecutor(this);
        plugin.getCommand("bank").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("world16.bank")) {
            player.sendMessage(Translate.miniMessage("<red>✖ You do not have permission to use this command."));
            return true;
        }

        if (args.length == 1) {
            String sub = args[0].toLowerCase();
            if (sub.equals("open")) {
                BankListGUI.open(plugin, player);
                return true;
            }
            // Try to match account name
            List<BankAccount> accounts = plugin.getBankManager().getAccountsForPlayer(player.getUniqueId());
            for (BankAccount account : accounts) {
                if (account.getName().equalsIgnoreCase(args[0])) {
                    BankAccountGUI.open(plugin, player, account);
                    return true;
                }
            }
        }

        BankListGUI.open(plugin, player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        if (args.length == 1) {
            List<String> options = new java.util.ArrayList<>();
            options.add("open");
            plugin.getBankManager().getAccountsForPlayer(player.getUniqueId()).stream()
                    .map(BankAccount::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                    .forEach(options::add);
            return options;
        }
        return List.of();
    }
}
