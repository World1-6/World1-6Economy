package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankTransaction;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BankTransactionHistoryGUI {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd HH:mm");

    public static void open(World16Economy plugin, Player player, BankAccount account) {
        List<BankTransaction> txList = plugin.getBankManager().getTransactions(account);
        List<CloneableGUIButton> buttons = new ArrayList<>();

        for (BankTransaction tx : txList) {
            Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(tx.getCurrencyUUID());
            String currencyColor = currency != null ? currency.getColor() : "<white>";
            String currencyName = currency != null ? currency.getCurrencyNamePlural() : "Unknown";

            OfflinePlayer actor = Bukkit.getOfflinePlayer(tx.getActorUUID());
            String actorName = actor.getName() != null ? actor.getName() : tx.getActorUUID().toString().substring(0, 8);

            Material icon = switch (tx.getType()) {
                case DEPOSIT, PAYROLL_IN, TRANSFER_IN -> Material.LIME_STAINED_GLASS_PANE;
                case WITHDRAWAL, PAYROLL_OUT, TRANSFER_OUT -> Material.RED_STAINED_GLASS_PANE;
            };

            String typeLabel = switch (tx.getType()) {
                case DEPOSIT -> "<green>⬆ Deposit";
                case WITHDRAWAL -> "<red>⬇ Withdrawal";
                case PAYROLL_OUT -> "<gold>⬇ Payroll Out";
                case PAYROLL_IN -> "<green>⬆ Payroll In";
                case TRANSFER_OUT -> "<red>⬇ Transfer Out";
                case TRANSFER_IN -> "<green>⬆ Transfer In";
            };

            String date = DATE_FORMAT.format(new Date(tx.getTimestamp()));
            String amount = String.format("%,.2f", tx.getAmount());

            buttons.add(new NoEventButton(0, InventoryUtils.createItem(icon, 1,
                    Translate.miniMessage(typeLabel).decoration(TextDecoration.ITALIC, false),
                    Translate.miniMessage("<dark_gray>▸ <gray>Amount: " + currencyColor + amount + " " + currencyName),
                    Translate.miniMessage("<dark_gray>▸ <gray>By: <white>" + actorName),
                    Translate.miniMessage("<dark_gray>▸ <gray>Date: <white>" + date))));
        }

        if (buttons.isEmpty()) {
            buttons.add(new NoEventButton(0, InventoryUtils.createItem(Material.PAPER, 1,
                    Translate.miniMessage("<gray>No transactions yet."))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Transactions: <white>" + account.getName()), buttons);

        gui.getCustomBottomButtons().add(new ClickEventButton(46,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, account);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
