package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.VaultCore;
import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.managers.UserWalletManager;
import com.andrew121410.mc.world16economy.objects.UserWallet;
import com.andrew121410.mc.world16economy.utils.API;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.utils.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class eco implements CommandExecutor {

    private final Map<UUID, UserWallet> userWalletMap;

    private final World16Economy plugin;

    private final UserWalletManager userWalletManager;

    private final VaultCore vaultCore;
    private final API api;

    public eco(World16Economy plugin) {
        this.plugin = plugin;
        this.userWalletMap = this.plugin.getUserWalletManager().getUserWalletMap();
        this.api = this.plugin.getApi();
        this.userWalletManager = this.plugin.getUserWalletManager();
        this.vaultCore = this.plugin.getVaultManager().getVaultCore();

        this.plugin.getCommand("eco").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        if (!player.hasPermission("world16.eco")) {
            player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Translate.color("&6/eco give <Player> <Amount>"));
            player.sendMessage(Translate.color("&6/eco take <Player> <Amount>"));
            player.sendMessage(Translate.color("&6/eco set <Player> <Amount>"));
            player.sendMessage(Translate.color("&6/eco reset <Player>"));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (!player.hasPermission("world16.eco.give")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            Long amount = Utils.asLongOrElse(args[2], null);

            if (amount == null) {
                player.sendMessage(Translate.color("Not a valid long(int)"));
                return true;
            }

            if (!targetChecker(player, target)) return true;

            if (amount == 0) {
                player.sendMessage(Translate.color("&cThe amount can't be 0."));
                return true;
            }

            vaultCore.depositPlayer(target.getUniqueId().toString(), (double) amount);
            player.sendMessage(Translate.color("&a$" + amount + " has been added to " + target.getDisplayName() + " account. &9New Balance: &a$" + userWalletMap.get(target.getUniqueId()).getBalance()));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("take")) {
            if (!player.hasPermission("world16.eco.take")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            Long amount = Utils.asLongOrElse(args[2], null);

            if (amount == null) {
                player.sendMessage(Translate.color("Not a valid long(int)"));
                return true;
            }

            if (!targetChecker(player, target)) return true;

            if (amount == 0) {
                player.sendMessage(Translate.color("&cThe amount can't be 0."));
                return true;
            }

            if (vaultCore.withdrawPlayer(target.getUniqueId().toString(), amount).type == EconomyResponse.ResponseType.SUCCESS) {
                player.sendMessage(Translate.color("&e$" + amount + " &ahas been taken from " + target.getDisplayName() + " account. &9New balance: &e$" + userWalletMap.get(target.getUniqueId()).getBalance()));
                return true;
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("world16.eco.set")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);
            Long amount = Utils.asLongOrElse(args[2], null);

            if (amount == null) {
                player.sendMessage(Translate.color("Not a valid long(int)"));
                return true;
            }

            if (!targetChecker(player, target)) return true;

            if (amount == 0) {
                player.sendMessage(Translate.color("&cThe amount can't be 0."));
                return true;
            }

            userWalletMap.get(target.getUniqueId()).setBalance(amount);
            target.sendMessage(Translate.color("&aYour balance was set to $" + amount));
            player.sendMessage(Translate.color("&aYou set " + target.getDisplayName() + "'s balance to $" + amount));
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (!player.hasPermission("world16.eco.reset")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[1]);

            if (!targetChecker(player, target)) return true;

            userWalletMap.get(target.getUniqueId()).setBalance(api.getDefaultMoney());
            target.sendMessage(Translate.color("&aYour balance was set to $" + api.getDefaultMoney()));
            player.sendMessage(Translate.color("&aYou set " + target.getDisplayName() + "'s balance to $" + api.getDefaultMoney()));
            return true;
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
        if (!userWalletManager.isUserMap(targetPlayer.getUniqueId())) {
            throw new NullPointerException("User isn't in memory?");
        }
        return true;
    }
}