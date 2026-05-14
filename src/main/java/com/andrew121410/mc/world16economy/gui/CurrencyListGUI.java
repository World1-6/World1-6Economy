package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CurrencyListGUI {

    private static final TextColor GREEN_DARK  = TextColor.color(0x00C853);
    private static final TextColor GREEN_LIGHT = TextColor.color(0x69FF47);

    public static void open(World16Economy plugin, Player player) {
        List<CloneableGUIButton> buttons = new ArrayList<>();

        for (Currency currency : plugin.getCurrenciesManager().getCurrenciesByUUID().values()) {
            boolean isDefault = currency.getUuid().equals(plugin.getCurrenciesManager().getDefaultCurrencyUUID());

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(currency.getItemMaterial(), 1,
                            Translate.miniMessage("<bold>" + currency.getColor() + currency.getName())
                                    .decoration(TextDecoration.ITALIC, false),
                            Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Singular: <white>" + currency.getCurrencyNameSingular()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Plural: <white>" + currency.getCurrencyNamePlural()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Default balance: <white>" + currency.getDefaultMoney()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Mob drops: <white>" + currency.getMobDropManager().getMobDropEntries().size()),
                            Component.empty(),
                            isDefault
                                    ? Translate.miniMessage("<green>✔ Default currency")
                                    : Translate.miniMessage("<yellow>⚙ Click to edit")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        CurrencyEditGUI.open(plugin, player, currency);
                    })
            ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                    Animation.wave(currency.getName(), TextColor.color(0xFFD700), TextColor.color(0xFF8C00))
                            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                    Translate.miniMessage("<dark_gray>▸ <gray>Symbol: <white>" + currency.getSymbol()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Singular: <white>" + currency.getCurrencyNameSingular()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Plural: <white>" + currency.getCurrencyNamePlural()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Default balance: <white>" + currency.getDefaultMoney()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Mob drops: <white>" + currency.getMobDropManager().getMobDropEntries().size()),
                    Component.empty(),
                    isDefault
                            ? Translate.miniMessage("<green>✔ Default currency")
                            : Translate.miniMessage("<yellow>⚙ Click to edit"))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Currencies"), buttons);

        gui.getCustomBottomButtons().add(((ChatResponseButton) new ChatResponseButton(47,
                InventoryUtils.createItem(Material.EMERALD, 1,
                        Translate.miniMessage("<green><bold>Create New Currency"),
                        Translate.miniMessage("<gray>Type the internal name in chat"),
                        Translate.miniMessage("<dark_gray>e.g. <white>tokens")),
                (Component) null, (Component) null,
                (p, input) -> {
                    String name = input.trim().toLowerCase().replace(" ", "-");
                    if (plugin.getCurrenciesManager().hasCurrencyByName(name)) {
                        p.sendMessage(Translate.miniMessage("<red>✖ A currency with that name already exists."));
                        CurrencyListGUI.open(plugin, p);
                        return;
                    }
                    Currency newCurrency = new Currency(name, "?", "Token", "Tokens", 0);
                    plugin.getCurrenciesManager().addCurrency(newCurrency);
                    plugin.getStorageManager().saveAllCurrencies();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, newCurrency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.EMERALD, 1,
                Animation.wave("✦ Create New Currency", GREEN_DARK, GREEN_LIGHT),
                Translate.miniMessage("<gray>Type the internal name in chat"),
                Translate.miniMessage("<dark_gray>e.g. <white>tokens"))));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
