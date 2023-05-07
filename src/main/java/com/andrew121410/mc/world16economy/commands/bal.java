package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        if (args.length == 0) {
            if (!player.hasPermission("world16.bal")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }

            this.showAllCurrenciesGUI(player, player);
            return true;
        } else if (args.length == 1) {
            if (!player.hasPermission("world16.bal.other")) {
                player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
                return true;
            }

            OfflinePlayer target = this.plugin.getServer().getOfflinePlayerIfCached(args[0]);
            if (target == null) {
                player.sendMessage(Translate.color("&cThat player does not exist."));
                return true;
            }

            this.showAllCurrenciesGUI(target, player);
            return true;
        }

        return true;
    }

    private void showAllCurrenciesGUI(OfflinePlayer target, Player player) {
        Wallet wallet = this.walletManager.getWallets().get(target.getUniqueId());

        if (wallet == null) {
            wallet = this.plugin.getStorageManager().loadWallet(target.getUniqueId(), false, false);
        }
        if (wallet == null) {
            player.sendMessage(Translate.miniMessage("<red>That player most likely doesn't exist in the database."));
            return;
        }

        Wallet finalWallet = wallet;
        MiddleGUIWindow guiWindow = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                int slot = 0; // Doesn't matter
                for (Map.Entry<UUID, CurrencyWallet> uuidCurrencyWalletEntry : finalWallet.getCurrencyWallets().entrySet()) {
                    UUID uuid = uuidCurrencyWalletEntry.getKey();
                    CurrencyWallet currencyWallet = uuidCurrencyWalletEntry.getValue();
                    Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(uuid);

                    Component itemName = Translate.miniMessage("<bold>" + currency.getColor() + currencyWallet.getBalanceDecimalFormat() + " " + currency.getCurrencyNamePlural());
                    itemName = itemName.decoration(TextDecoration.ITALIC, false); // Why is it italic anyway?

                    ItemStack itemStack = InventoryUtils.createItem(currency.getItemMaterial(), 1, itemName, Component.empty());

                    ClickEventButton button = new ClickEventButton(slot, itemStack, (event) -> {
                    });

                    slot += 1; // Doesn't matter

                    buttons.add(button);
                }

                Component title = target.getUniqueId() == player.getUniqueId() ? Translate.miniMessage("Your wallet!") : Translate.miniMessage(target.getName() + "'s wallet!");

                this.update(buttons, title, null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {
            }
        };

        guiWindow.open(player);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
    }
}
