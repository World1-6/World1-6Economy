package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.currency.MobDropEntry;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MobDropListGUI {

    private static final TextColor GREEN      = TextColor.color(0x00C853);
    private static final TextColor GREEN_LIME = TextColor.color(0x69FF47);
    private static final TextColor GOLD       = TextColor.color(0xFFD700);
    private static final TextColor ORANGE     = TextColor.color(0xFF8C00);

    public static void open(World16Economy plugin, Player player, Currency currency) {
        List<CloneableGUIButton> buttons = new ArrayList<>();

        buttons.add(((ClickEventButton) new ClickEventButton(0,
                InventoryUtils.createItem(Material.EMERALD, 1,
                        Translate.miniMessage("<green><bold>Add New Entry"),
                        Translate.miniMessage("<gray>Adds a new mob drop entry"),
                        Translate.miniMessage("<gray>with default values")),
                event -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    MobDropEntry newEntry = new MobDropEntry(1, 10, 1.0);
                    currency.getMobDropManager().getMobDropEntries().add(newEntry);
                    plugin.getStorageManager().saveAllCurrencies();
                    MobDropEntryEditGUI.open(plugin, player, currency, newEntry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.EMERALD, 1,
                Animation.wave("✦ Add New Entry", GREEN, GREEN_LIME),
                Translate.miniMessage("<gray>Adds a new mob drop entry"),
                Translate.miniMessage("<gray>with default values"))));

        for (MobDropEntry entry : currency.getMobDropManager().getMobDropEntries()) {
            String entityList = entry.getEntityTypes().isEmpty()
                    ? "All mobs"
                    : entry.getEntityTypes().stream().map(EntityType::name).collect(Collectors.joining(", "));

            String modeText = entry.isDropAsItem() ? "<green>⬆ Physical note drop" : "<yellow>⬆ Direct wallet credit";

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(Material.CREEPER_HEAD, 1,
                            Translate.miniMessage("<white><bold>Drop Entry")
                                    .decoration(TextDecoration.ITALIC, false),
                            Translate.miniMessage("<dark_gray>▸ <gray>Amount: <white>" + entry.getMin() + " — " + entry.getMax()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Chance: <white>" + (entry.getChance() * 100) + "%"),
                            Translate.miniMessage("<dark_gray>▸ <gray>Mobs: <white>" + entityList),
                            Translate.miniMessage("<dark_gray>▸ <gray>Mode: " + modeText),
                            Component.empty(),
                            Translate.miniMessage("<yellow>⚙ Click to edit")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        MobDropEntryEditGUI.open(plugin, player, currency, entry);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.CREEPER_HEAD, 1,
                    Animation.fading("Drop Entry", GOLD, ORANGE)
                            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                    Translate.miniMessage("<dark_gray>▸ <gray>Amount: <white>" + entry.getMin() + " — " + entry.getMax()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Chance: <white>" + (entry.getChance() * 100) + "%"),
                    Translate.miniMessage("<dark_gray>▸ <gray>Mobs: <white>" + entityList),
                    Translate.miniMessage("<dark_gray>▸ <gray>Mode: " + modeText),
                    Component.empty(),
                    Translate.miniMessage("<yellow>⚙ Click to edit"))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Mob Drops: <yellow>" + currency.getName()), buttons);

        gui.getCustomBottomButtons().add(new ClickEventButton(47,
                InventoryUtils.createItem(Material.ARROW, 1,
                        Translate.miniMessage("<gray>← Back"),
                        Translate.miniMessage("<dark_gray>Return to currency editor")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, player, currency);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
