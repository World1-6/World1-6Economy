package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class withdraw implements CommandExecutor, TabCompleter {

    private final World16Economy plugin;

    public withdraw(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("withdraw").setExecutor(this);
        this.plugin.getCommand("withdraw").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("world16.withdraw")) {
            player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Translate.miniMessage("<yellow>Usage: /withdraw <amount> [currency]"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Translate.miniMessage("<red>Invalid amount."));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(Translate.miniMessage("<red>Amount must be greater than zero."));
            return true;
        }

        // Resolve currency — default if not specified, otherwise by name
        Currency currency;
        if (args.length >= 2) {
            currency = plugin.getCurrenciesManager().getCurrencyByName(args[1].toLowerCase());
            if (currency == null) {
                player.sendMessage(Translate.miniMessage("<red>Unknown currency: <white>" + args[1]));
                return true;
            }
        } else {
            currency = plugin.getCurrenciesManager().getDefaultCurrency();
        }

        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        if (wallet == null) {
            player.sendMessage(Translate.miniMessage("<red>Could not find your wallet. Try rejoining."));
            return true;
        }

        CurrencyWallet currencyWallet = wallet.getCurrencyWallets().get(currency.getUuid());
        if (currencyWallet == null || !currencyWallet.hasRequiredAmount(amount)) {
            player.sendMessage(Translate.miniMessage("<red>You don't have enough " + currency.getCurrencyNamePlural() + "."));
            return true;
        }

        // Check the player has inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Translate.miniMessage("<red>Your inventory is full."));
            return true;
        }

        // Deduct balance, then create the note item — order matters to avoid giving item on error
        currencyWallet.subtractAmount(amount);

        ItemStack noteItem = plugin.getNoteManager().createNote(player.getUniqueId(), currency, amount);
        if (noteItem == null) {
            // DB write failed — refund the balance
            currencyWallet.addAmount(amount);
            player.sendMessage(Translate.miniMessage("<red>Failed to create currency note. Please try again."));
            return true;
        }

        player.getInventory().addItem(noteItem);

        String amountFormatted = amount % 1 == 0 ? String.valueOf((long) amount) : String.valueOf(amount);
        String currencyName = amount == 1 ? currency.getCurrencyNameSingular() : currency.getCurrencyNamePlural();
        player.sendMessage(Translate.miniMessage(
                "<green>Withdrew " + currency.getColor() + amountFormatted + " " + currencyName
                        + "<green> as a note. Right-click it to redeem."));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("100");
            completions.add("500");
            completions.add("1000");
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            plugin.getCurrenciesManager().getCurrenciesByUUID().values().forEach(c -> {
                if (c.getName().startsWith(input)) completions.add(c.getName());
            });
        }
        return completions;
    }
}
