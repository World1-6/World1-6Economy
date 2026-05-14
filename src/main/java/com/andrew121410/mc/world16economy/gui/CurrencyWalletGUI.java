package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CurrencyWalletGUI {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);

    public static void open(World16Economy plugin, Player player, Currency currency, CurrencyWallet currencyWallet) {
        MiddleGUIWindow gui = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                int slot = 0;
                String balance = currencyWallet.getBalanceDecimalFormat();
                String currencyName = currencyWallet.getBalanceExact() == 1
                        ? currency.getCurrencyNameSingular()
                        : currency.getCurrencyNamePlural();

                buttons.add(((NoEventButton) new NoEventButton(slot++,
                        InventoryUtils.createItem(currency.getItemMaterial(), 1,
                                Translate.miniMessage("<bold>" + currency.getColor() + balance + " " + currencyName)
                                        .decoration(TextDecoration.ITALIC, false),
                                Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                                Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName())))
                ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                        Animation.wave(balance + " " + currencyName, GOLD, ORANGE)
                                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                        Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName()))));

                buttons.add(((ChatResponseButton) new ChatResponseButton(slot++,
                        InventoryUtils.createItem(Material.CHEST, 1,
                                Translate.miniMessage("<green><bold>Withdraw"),
                                Translate.miniMessage("<gray>Convert " + currency.getColor() + currencyName + " <gray>into a physical note"),
                                Translate.miniMessage("<dark_gray>▸ <gray>Balance: " + currency.getColor() + balance)),
                        (net.kyori.adventure.text.Component) null, null,
                        (p, input) -> {
                            double amount;
                            try {
                                amount = Double.parseDouble(input.trim());
                            } catch (NumberFormatException e) {
                                p.sendMessage(Translate.miniMessage("<red>✖ Invalid amount."));
                                CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                                return;
                            }

                            if (amount <= 0) {
                                p.sendMessage(Translate.miniMessage("<red>✖ Amount must be greater than zero."));
                                CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                                return;
                            }

                            if (!currencyWallet.hasRequiredAmount(amount)) {
                                p.sendMessage(Translate.miniMessage("<red>✖ You don't have enough "
                                        + currency.getCurrencyNamePlural() + "."));
                                CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                                return;
                            }

                            if (p.getInventory().firstEmpty() == -1) {
                                p.sendMessage(Translate.miniMessage("<red>✖ Your inventory is full."));
                                CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                                return;
                            }

                            currencyWallet.subtractAmount(amount);
                            Wallet walletToSave = plugin.getWalletManager().getWallets().get(p.getUniqueId());
                            if (walletToSave != null) plugin.getStorageManager().saveWallet(walletToSave);

                            ItemStack noteItem = plugin.getNoteManager().createNote(p.getUniqueId(), currency, amount);
                            if (noteItem == null) {
                                currencyWallet.addAmount(amount);
                                p.sendMessage(Translate.miniMessage("<red>✖ Failed to create note. Please try again."));
                                CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                                return;
                            }

                            p.getInventory().addItem(noteItem);

                            String amountFormatted = amount % 1 == 0
                                    ? String.valueOf((long) amount)
                                    : String.valueOf(amount);
                            String name = amount == 1
                                    ? currency.getCurrencyNameSingular()
                                    : currency.getCurrencyNamePlural();
                            p.sendMessage(Translate.miniMessage("<green>✔ Withdrew "
                                    + currency.getColor() + amountFormatted + " " + name
                                    + "<green> as a note."));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

                            CurrencyWalletGUI.open(plugin, p, currency, currencyWallet);
                        })
                ).animate(() -> InventoryUtils.createItem(Material.CHEST, 1,
                        Animation.wave("Withdraw", GREEN, LIME),
                        Translate.miniMessage("<gray>Convert " + currency.getColor() + currencyName + " <gray>into a physical note"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Balance: " + currency.getColor() + balance))));

                buttons.add(new ClickEventButton(slot, InventoryUtils.createItem(
                        Material.ARROW, 1,
                        Translate.miniMessage("<gray>← Back")),
                        event -> player.closeInventory()));

                this.update(buttons,
                        Translate.miniMessage("<dark_gray>✦ " + currency.getColor() + currency.getName()),
                        null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {}
        };

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
