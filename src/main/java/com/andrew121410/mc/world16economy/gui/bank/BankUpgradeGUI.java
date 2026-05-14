package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankTier;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class BankUpgradeGUI {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);

    public static void open(World16Economy plugin, Player player, BankAccount account) {
        BankTier current = plugin.getBankManager().getTier(account.getTierLevel());
        BankTier next = plugin.getBankManager().getNextTier(account.getTierLevel());

        MiddleGUIWindow gui = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                // Current tier info
                buttons.add(new NoEventButton(0, InventoryUtils.createItem(Material.IRON_INGOT, 1,
                        Translate.miniMessage("<white><bold>Current: " + (current != null ? current.getDisplayName() : "Unknown")),
                        current != null && current.hasBalanceLimit()
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>" + String.format("%,.0f", current.getBalanceLimit()))
                                : Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>Unlimited"),
                        current != null && current.hasTransactionLimit()
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>" + String.format("%,.0f", current.getTransactionLimit()))
                                : Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>Unlimited"))));

                // Next tier + upgrade button
                if (next != null) {
                    buttons.add(((ClickEventButton) new ClickEventButton(0,
                            InventoryUtils.createItem(Material.NETHER_STAR, 1,
                                    Translate.miniMessage("<gold><bold>Upgrade to " + next.getDisplayName()),
                                    Translate.miniMessage("<dark_gray>▸ <gray>Cost: <yellow>" + String.format("%,.0f", next.getCreationCost())),
                                    next.hasBalanceLimit()
                                            ? Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>" + String.format("%,.0f", next.getBalanceLimit()))
                                            : Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>Unlimited"),
                                    next.hasTransactionLimit()
                                            ? Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>" + String.format("%,.0f", next.getTransactionLimit()))
                                            : Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>Unlimited"),
                                    Translate.miniMessage("<yellow>⚙ Click to upgrade")),
                            event -> {
                                Currency defaultCurrency = plugin.getCurrenciesManager()
                                        .getCurrencyByUUID(plugin.getCurrenciesManager().getDefaultCurrencyUUID());
                                if (defaultCurrency != null && next.getCreationCost() > 0) {
                                    Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
                                    CurrencyWallet cw = wallet != null
                                            ? wallet.getCurrencyWallets().get(defaultCurrency.getUuid()) : null;
                                    if (cw == null || !cw.hasRequiredAmount(next.getCreationCost())) {
                                        player.sendMessage(Translate.miniMessage("<red>✖ You need <white>"
                                                + String.format("%,.0f", next.getCreationCost()) + " "
                                                + defaultCurrency.getCurrencyNamePlural() + " <red>to upgrade."));
                                        BankUpgradeGUI.open(plugin, player, account);
                                        return;
                                    }
                                    cw.subtractAmount(next.getCreationCost());
                                    Wallet w2 = plugin.getWalletManager().getWallets().get(player.getUniqueId());
                                    if (w2 != null) plugin.getStorageManager().saveWallet(w2);
                                }
                                account.setTierLevel(next.getLevel());
                                plugin.getBankManager().saveAccount(account);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                player.sendMessage(Translate.miniMessage("<green>✔ Upgraded to <white>" + next.getDisplayName() + "<green>!"));
                                BankAccountGUI.open(plugin, player, account);
                            })
                    ).animate(() -> InventoryUtils.createItem(Material.NETHER_STAR, 1,
                            Animation.wave("⬆ Upgrade to " + next.getDisplayName(), GOLD, ORANGE),
                            Translate.miniMessage("<dark_gray>▸ <gray>Cost: <yellow>" + String.format("%,.0f", next.getCreationCost())),
                            next.hasBalanceLimit()
                                    ? Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>" + String.format("%,.0f", next.getBalanceLimit()))
                                    : Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>Unlimited"))));
                } else {
                    buttons.add(new NoEventButton(0, InventoryUtils.createItem(Material.BEACON, 1,
                            Translate.miniMessage("<green><bold>✔ Max Tier Reached"),
                            Translate.miniMessage("<gray>This account is at the highest tier."))));
                }

                // Back
                buttons.add(new ClickEventButton(0,
                        InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            BankAccountGUI.open(plugin, player, account);
                        }));

                this.update(buttons, Translate.miniMessage("<dark_gray>✦ Upgrade: <white>" + account.getName()), null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {}
        };

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
