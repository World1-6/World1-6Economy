package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WalletListGUI extends MiddleGUIWindow {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);

    private final World16Economy plugin;
    private final OfflinePlayer target;
    private final Player viewer;
    private final Wallet wallet;

    private WalletListGUI(World16Economy plugin, OfflinePlayer target, Player viewer, Wallet wallet) {
        this.plugin = plugin;
        this.target = target;
        this.viewer = viewer;
        this.wallet = wallet;
    }

    public static void open(World16Economy plugin, OfflinePlayer target, Player viewer) {
        Wallet wallet = plugin.getWalletManager().getWallets().get(target.getUniqueId());
        if (wallet == null) {
            viewer.sendMessage(Translate.miniMessage("<red>✖ That player has no wallet data."));
            return;
        }
        viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new WalletListGUI(plugin, target, viewer, wallet).open(viewer);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();
        boolean isOwn = target.getUniqueId().equals(viewer.getUniqueId());

        int slot = 0;
        for (Map.Entry<UUID, CurrencyWallet> entry : wallet.getCurrencyWallets().entrySet()) {
            UUID uuid = entry.getKey();
            CurrencyWallet cw = entry.getValue();
            Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(uuid);
            if (currency == null) continue;

            String balance = cw.getBalanceDecimalFormat();
            String currencyName = cw.getBalanceExact() == 1
                    ? currency.getCurrencyNameSingular()
                    : currency.getCurrencyNamePlural();
            String displayName = currency.getColor() + balance + " " + currencyName;

            if (isOwn) {
                final CurrencyWallet finalCw = cw;
                buttons.add(((ClickEventButton) new ClickEventButton(slot,
                        InventoryUtils.createItem(currency.getItemMaterial(), 1,
                                Translate.miniMessage("<bold>" + displayName)
                                        .decoration(TextDecoration.ITALIC, false),
                                Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                                Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName()),
                                Translate.miniMessage("<dark_gray>▸ <gray>Click to manage")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            CurrencyWalletGUI.open(plugin, player, currency, finalCw);
                        })
                ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                        Animation.wave(balance + " " + currencyName, GOLD, ORANGE)
                                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                        Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Click to manage"))));
            } else {
                buttons.add(((NoEventButton) new NoEventButton(slot,
                        InventoryUtils.createItem(currency.getItemMaterial(), 1,
                                Translate.miniMessage("<bold>" + displayName)
                                        .decoration(TextDecoration.ITALIC, false),
                                Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                                Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName())))
                ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                        Animation.wave(balance + " " + currencyName, AQUA, BLUE)
                                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                        Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName()))));
            }

            slot++;
        }

        String title = isOwn
                ? "<dark_gray>✦ <gold>Your Wallet"
                : "<dark_gray>✦ <white>" + target.getName() + "<dark_gray>'s <gold>Wallet";
        this.update(buttons, Translate.miniMessage(title), null);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
