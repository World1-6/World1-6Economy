package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankAccountType;
import com.andrew121410.mc.world16economy.bank.BankTier;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class BankCreateGUI {

    public static void open(World16Economy plugin, Player player, BankAccountType type) {
        BankTier tier1 = plugin.getBankManager().getTier(1);
        double cost = tier1 != null ? tier1.getCreationCost() : 0;
        String typeName = type == BankAccountType.PERSONAL ? "Personal" : "Business";
        Material icon = type == BankAccountType.PERSONAL ? Material.ENDER_CHEST : Material.GILDED_BLACKSTONE;

        MiddleGUIWindow gui = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                // Info
                buttons.add(new NoEventButton(0, InventoryUtils.createItem(icon, 1,
                        Translate.miniMessage("<bold><white>New " + typeName + " Account"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Tier 1 creation cost: <yellow>" + String.format("%,.0f", cost)),
                        tier1 != null && tier1.hasBalanceLimit()
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>" + String.format("%,.0f", tier1.getBalanceLimit()))
                                : Translate.miniMessage("<dark_gray>▸ <gray>Balance limit: <white>Unlimited"),
                        tier1 != null && tier1.hasTransactionLimit()
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>" + String.format("%,.0f", tier1.getTransactionLimit()))
                                : Translate.miniMessage("<dark_gray>▸ <gray>Transaction limit: <white>Unlimited"))));

                // Name input
                buttons.add(new ChatResponseButton(0, InventoryUtils.createItem(Material.NAME_TAG, 1,
                        Translate.miniMessage("<green><bold>Create Account"),
                        Translate.miniMessage("<gray>Type the account name in chat")),
                        (Component) null, (Component) null,
                        (p, input) -> {
                            String name = input.trim();
                            if (name.isEmpty()) {
                                p.sendMessage(Translate.miniMessage("<red>✖ Account name cannot be empty."));
                                BankCreateGUI.open(plugin, p, type);
                                return;
                            }

                            // Check creation cost using default currency
                            if (cost > 0) {
                                Currency defaultCurrency = plugin.getCurrenciesManager()
                                        .getCurrencyByUUID(plugin.getCurrenciesManager().getDefaultCurrencyUUID());
                                if (defaultCurrency != null) {
                                    Wallet wallet = plugin.getWalletManager().getWallets().get(p.getUniqueId());
                                    CurrencyWallet cw = wallet != null
                                            ? wallet.getCurrencyWallets().get(defaultCurrency.getUuid()) : null;
                                    if (cw == null || !cw.hasRequiredAmount(cost)) {
                                        p.sendMessage(Translate.miniMessage("<red>✖ You need <white>"
                                                + String.format("%,.0f", cost) + " "
                                                + defaultCurrency.getCurrencyNamePlural()
                                                + " <red>to create a bank account."));
                                        BankCreateGUI.open(plugin, p, type);
                                        return;
                                    }
                                    cw.subtractAmount(cost);
                                    Wallet w = plugin.getWalletManager().getWallets().get(p.getUniqueId());
                                    if (w != null) plugin.getStorageManager().saveWallet(w);
                                }
                            }

                            BankAccount account = plugin.getBankManager().createAccount(p.getUniqueId(), name, type);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            p.sendMessage(Translate.miniMessage("<green>✔ Created bank account: <white>" + name));
                            BankAccountGUI.open(plugin, p, account);
                        }));

                // Back
                buttons.add(new ClickEventButton(0, InventoryUtils.createItem(Material.ARROW, 1,
                        Translate.miniMessage("<gray>← Back")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            BankListGUI.open(plugin, player);
                        }));

                this.update(buttons, Translate.miniMessage("<dark_gray>✦ New " + typeName + " Account"), null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {}
        };

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
