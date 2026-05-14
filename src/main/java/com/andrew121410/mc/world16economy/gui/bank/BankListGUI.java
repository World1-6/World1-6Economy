package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankAccountType;
import com.andrew121410.mc.world16economy.bank.BankRole;
import com.andrew121410.mc.world16economy.bank.BankTier;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankListGUI {

    private static final TextColor GOLD      = TextColor.color(0xFFD700);
    private static final TextColor ORANGE    = TextColor.color(0xFF8C00);
    private static final TextColor AQUA      = TextColor.color(0x00E5FF);
    private static final TextColor BLUE      = TextColor.color(0x2979FF);
    private static final TextColor GREEN     = TextColor.color(0x00C853);
    private static final TextColor GREEN_LT  = TextColor.color(0x69FF47);
    private static final TextColor PURPLE    = TextColor.color(0xAA00FF);
    private static final TextColor PURPLE_LT = TextColor.color(0xE040FB);

    public static void open(World16Economy plugin, Player player) {
        List<CloneableGUIButton> buttons = new ArrayList<>();
        List<BankAccount> playerAccounts = plugin.getBankManager().getAccountsForPlayer(player.getUniqueId());

        for (BankAccount account : playerAccounts) {
            BankRole role = account.getRoleOf(player.getUniqueId());
            BankTier tier = plugin.getBankManager().getTier(account.getTierLevel());
            String tierName = tier != null ? tier.getDisplayName() : "Tier " + account.getTierLevel();

            boolean isBusiness = account.isBusiness();
            Material icon = isBusiness ? Material.GILDED_BLACKSTONE : Material.ENDER_CHEST;
            TextColor waveA = isBusiness ? PURPLE : AQUA;
            TextColor waveB = isBusiness ? PURPLE_LT : BLUE;

            // Build balance summary lines
            List<Component> lore = new ArrayList<>();
            lore.add(Translate.miniMessage("<dark_gray>▸ <gray>Type: <white>" + (isBusiness ? "Business" : "Personal")));
            lore.add(Translate.miniMessage("<dark_gray>▸ <gray>Tier: <white>" + tierName));
            lore.add(Translate.miniMessage("<dark_gray>▸ <gray>Role: <white>" + role.name()));
            if (isBusiness) {
                lore.add(Translate.miniMessage("<dark_gray>▸ <gray>Members: <white>" + account.getMembers().size()));
            }

            CurrenciesManager cm = plugin.getCurrenciesManager();
            account.getBalances().forEach((currencyUUID, balance) -> {
                Currency currency = cm.getCurrencyByUUID(currencyUUID);
                if (currency == null) return;
                String formatted = String.format("%,.0f", balance);
                lore.add(Translate.miniMessage("<dark_gray>▸ " + currency.getColor() + formatted + " " + currency.getCurrencyNamePlural()));
            });

            lore.add(Component.empty());
            lore.add(Translate.miniMessage("<yellow>⚙ Click to open"));

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(icon, 1,
                            Translate.miniMessage("<bold><white>" + account.getName())
                                    .decoration(TextDecoration.ITALIC, false),
                            lore.toArray(new Component[0])),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankAccountGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(icon, 1,
                    Animation.wave(account.getName(), waveA, waveB)
                            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                    lore.toArray(new Component[0]))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Your Bank Accounts"), buttons);

        // Create Personal Account button
        gui.getCustomBottomButtons().add(((ClickEventButton) new ClickEventButton(46,
                InventoryUtils.createItem(Material.ENDER_CHEST, 1,
                        Translate.miniMessage("<aqua><bold>Open Personal Account"),
                        Translate.miniMessage("<gray>Create a new personal bank account")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankCreateGUI.open(plugin, player, BankAccountType.PERSONAL);
                })
        ).animate(() -> InventoryUtils.createItem(Material.ENDER_CHEST, 1,
                Animation.wave("✦ New Personal Account", AQUA, BLUE),
                Translate.miniMessage("<gray>Create a new personal bank account"))));

        // Create Business Account button
        gui.getCustomBottomButtons().add(((ClickEventButton) new ClickEventButton(47,
                InventoryUtils.createItem(Material.GILDED_BLACKSTONE, 1,
                        Translate.miniMessage("<light_purple><bold>Open Business Account"),
                        Translate.miniMessage("<gray>Create a new business bank account")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankCreateGUI.open(plugin, player, BankAccountType.BUSINESS);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GILDED_BLACKSTONE, 1,
                Animation.wave("✦ New Business Account", PURPLE, PURPLE_LT),
                Translate.miniMessage("<gray>Create a new business bank account"))));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
