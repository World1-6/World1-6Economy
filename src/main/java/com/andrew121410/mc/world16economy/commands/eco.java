package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.gui.CurrencyListGUI;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class eco implements CommandExecutor, TabCompleter {

    private final World16Economy plugin;

    public eco(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("eco").setExecutor(this);
        this.plugin.getCommand("eco").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            // Allow console for give/take/set/reset
            if (args.length >= 3) {
                handleSubcommand(sender, args);
            } else {
                sender.sendMessage("Usage: /eco <give|take|set|reset> <player> <amount> [currency]");
            }
            return true;
        }

        if (!player.hasPermission("world16.eco")) {
            player.sendMessage(Translate.miniMessage("<red>You do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            CurrencyListGUI.open(plugin, player);
            return true;
        }

        handleSubcommand(sender, args);
        return true;
    }

    private void handleSubcommand(CommandSender sender, String[] args) {
        String sub = args[0].toLowerCase();

        if (sub.equals("give") || sub.equals("take") || sub.equals("set") || sub.equals("reset")) {
            String perm = "world16.eco." + sub;
            if (sender instanceof Player p && !p.hasPermission(perm)) {
                sender.sendMessage(Translate.miniMessage("<red>You do not have permission to do that."));
                return;
            }
        }

        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (args.length < 3) { sender.sendMessage(Translate.miniMessage("<red>Usage: /eco give <player> <amount> [currency]")); return; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                double amount = parseAmount(sender, args[2]);
                if (amount <= 0) return;
                Currency currency = resolveCurrency(sender, args.length >= 4 ? args[3] : null);
                if (currency == null) return;
                Wallet wallet = getOrLoadWallet(target);
                wallet.getCurrencyWallets().computeIfAbsent(currency.getUuid(), uuid -> new CurrencyWallet(uuid, 0)).addAmount(amount);
                plugin.getStorageManager().saveWallet(wallet, false);
                sender.sendMessage(Translate.miniMessage("<green>✔ Gave <white>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural() + " <green>to <white>" + target.getName() + "<green>."));
                if (target.isOnline()) ((Player) target).sendMessage(Translate.miniMessage("<green>✔ You received <white>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural() + "<green>."));
            }
            case "take" -> {
                if (args.length < 3) { sender.sendMessage(Translate.miniMessage("<red>Usage: /eco take <player> <amount> [currency]")); return; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                double amount = parseAmount(sender, args[2]);
                if (amount <= 0) return;
                Currency currency = resolveCurrency(sender, args.length >= 4 ? args[3] : null);
                if (currency == null) return;
                Wallet wallet = getOrLoadWallet(target);
                CurrencyWallet cw = wallet.getCurrencyWallets().computeIfAbsent(currency.getUuid(), uuid -> new CurrencyWallet(uuid, 0));
                cw.subtractAmount(Math.min(amount, cw.getBalanceExact()));
                plugin.getStorageManager().saveWallet(wallet, false);
                sender.sendMessage(Translate.miniMessage("<green>✔ Took <white>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural() + " <green>from <white>" + target.getName() + "<green>."));
                if (target.isOnline()) ((Player) target).sendMessage(Translate.miniMessage("<red>✖ <white>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural() + " <red>was taken from your wallet."));
            }
            case "set" -> {
                if (args.length < 3) { sender.sendMessage(Translate.miniMessage("<red>Usage: /eco set <player> <amount> [currency]")); return; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                double amount = parseAmount(sender, args[2]);
                if (amount < 0) return;
                Currency currency = resolveCurrency(sender, args.length >= 4 ? args[3] : null);
                if (currency == null) return;
                Wallet wallet = getOrLoadWallet(target);
                wallet.getCurrencyWallets().computeIfAbsent(currency.getUuid(), uuid -> new CurrencyWallet(uuid, 0)).setBalanceExact(amount);
                plugin.getStorageManager().saveWallet(wallet, false);
                sender.sendMessage(Translate.miniMessage("<green>✔ Set <white>" + target.getName() + "<green>'s balance to <white>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural() + "<green>."));
            }
            case "reset" -> {
                if (args.length < 2) { sender.sendMessage(Translate.miniMessage("<red>Usage: /eco reset <player> [currency]")); return; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                Currency currency = resolveCurrency(sender, args.length >= 3 ? args[2] : null);
                if (currency == null) return;
                Wallet wallet = getOrLoadWallet(target);
                wallet.getCurrencyWallets().computeIfAbsent(currency.getUuid(), uuid -> new CurrencyWallet(uuid, 0)).setBalanceExact(0);
                plugin.getStorageManager().saveWallet(wallet, false);
                sender.sendMessage(Translate.miniMessage("<green>✔ Reset <white>" + target.getName() + "<green>'s " + currency.getName() + " balance to 0."));
            }
            default -> sender.sendMessage(Translate.miniMessage("<red>Unknown subcommand. Use: give, take, set, reset, or just /eco to open the GUI."));
        }
    }

    private double parseAmount(CommandSender sender, String input) {
        try {
            double v = Double.parseDouble(input);
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            sender.sendMessage(Translate.miniMessage("<red>✖ Invalid amount: <white>" + input));
            return -1;
        }
    }

    private Currency resolveCurrency(CommandSender sender, String name) {
        if (name == null) {
            Currency def = plugin.getCurrenciesManager().getCurrencyByUUID(plugin.getCurrenciesManager().getDefaultCurrencyUUID());
            if (def == null) { sender.sendMessage(Translate.miniMessage("<red>✖ No default currency set.")); }
            return def;
        }
        Currency found = plugin.getCurrenciesManager().getCurrenciesByUUID().values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name)
                        || c.getCurrencyNameSingular().equalsIgnoreCase(name)
                        || c.getCurrencyNamePlural().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (found == null) sender.sendMessage(Translate.miniMessage("<red>✖ Unknown currency: <white>" + name));
        return found;
    }

    private Wallet getOrLoadWallet(OfflinePlayer player) {
        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        if (wallet == null) {
            wallet = plugin.getStorageManager().loadWallet(player.getUniqueId(), false, true);
        }
        return wallet;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return List.of("give", "take", "set", "reset").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        if (args.length == 2) return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        if (args.length == 4) return plugin.getCurrenciesManager().getCurrenciesByUUID().values().stream()
                .map(c -> c.getName()).filter(n -> n.toLowerCase().startsWith(args[3].toLowerCase())).toList();
        return List.of();
    }
}
