package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.currency.MobDropEntry;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MobDropEntryEditGUI extends GUIWindow {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor RED    = TextColor.color(0xFF1744);
    private static final TextColor RED2   = TextColor.color(0xFF6D00);

    private final World16Economy plugin;
    private final Player player;
    private final Currency currency;
    private final MobDropEntry entry;

    public MobDropEntryEditGUI(World16Economy plugin, Player player, Currency currency, MobDropEntry entry) {
        this.plugin = plugin;
        this.player = player;
        this.currency = currency;
        this.entry = entry;
    }

    public static void open(World16Economy plugin, Player player, Currency currency, MobDropEntry entry) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new MobDropEntryEditGUI(plugin, player, currency, entry).open(player);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();

        buttons.add(((ChatResponseButton) new ChatResponseButton(10,
                InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                        Translate.miniMessage("<white><bold>Min Amount"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + entry.getMin()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Minimum currency dropped")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        long val = Long.parseLong(input.trim());
                        if (val < 0) throw new NumberFormatException();
                        entry.setMin(val);
                        save();
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid number. Must be a positive whole number."));
                    }
                    MobDropEntryEditGUI.open(plugin, p, currency, entry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                Animation.fading("Min Amount", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + entry.getMin()),
                Translate.miniMessage("<dark_gray>▸ <gray>Minimum currency dropped"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(12,
                InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                        Translate.miniMessage("<white><bold>Max Amount"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + entry.getMax()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Maximum currency dropped")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        long val = Long.parseLong(input.trim());
                        if (val < 0) throw new NumberFormatException();
                        entry.setMax(val);
                        save();
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid number. Must be a positive whole number."));
                    }
                    MobDropEntryEditGUI.open(plugin, p, currency, entry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                Animation.fading("Max Amount", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + entry.getMax()),
                Translate.miniMessage("<dark_gray>▸ <gray>Maximum currency dropped"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(14,
                InventoryUtils.createItem(Material.COMPASS, 1,
                        Translate.miniMessage("<white><bold>Chance"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + (entry.getChance() * 100) + "%"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Enter a value from <white>0 <gray>to <white>100")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        double val = Double.parseDouble(input.trim());
                        if (val < 0 || val > 100) throw new NumberFormatException();
                        entry.setChance(val / 100.0);
                        save();
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid number. Must be between 0 and 100."));
                    }
                    MobDropEntryEditGUI.open(plugin, p, currency, entry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.COMPASS, 1,
                Animation.fading("Chance", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + (entry.getChance() * 100) + "%"),
                Translate.miniMessage("<dark_gray>▸ <gray>Enter a value from <white>0 <gray>to <white>100"))));

        String currentTypes = entry.getEntityTypes().isEmpty()
                ? "All mobs"
                : entry.getEntityTypes().stream().map(EntityType::name).collect(Collectors.joining(", "));

        buttons.add(((ChatResponseButton) new ChatResponseButton(16,
                InventoryUtils.createItem(Material.CREEPER_HEAD, 1,
                        Translate.miniMessage("<white><bold>Entity Types"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currentTypes),
                        Translate.miniMessage("<dark_gray>▸ <gray>Comma-separated, e.g. <white>ZOMBIE, SKELETON"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Leave blank for all mobs")),
                (Component) null, (Component) null,
                (p, input) -> {
                    List<EntityType> entityTypes = new ArrayList<>();
                    if (!input.isBlank()) {
                        List<String> invalid = new ArrayList<>();
                        for (String part : input.split(",")) {
                            String trimmed = part.trim().toUpperCase();
                            try {
                                entityTypes.add(EntityType.valueOf(trimmed));
                            } catch (IllegalArgumentException e) {
                                invalid.add(trimmed);
                            }
                        }
                        if (!invalid.isEmpty()) {
                            p.sendMessage(Translate.miniMessage("<red>✖ Unknown entity types: <white>" + String.join(", ", invalid)));
                        }
                    }
                    entry.setEntityTypes(entityTypes);
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    MobDropEntryEditGUI.open(plugin, p, currency, entry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.CREEPER_HEAD, 1,
                Animation.fading("Entity Types", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currentTypes),
                Translate.miniMessage("<dark_gray>▸ <gray>Comma-separated, e.g. <white>ZOMBIE, SKELETON"),
                Translate.miniMessage("<dark_gray>▸ <gray>Leave blank for all mobs"))));

        buttons.add(((ClickEventButton) new ClickEventButton(19,
                InventoryUtils.createItem(Material.CHEST, 1,
                        Translate.miniMessage("<white><bold>Drop Mode"),
                        entry.isDropAsItem()
                                ? Translate.miniMessage("<green>⬆ Physical note drops at mob location")
                                : Translate.miniMessage("<yellow>⬆ Credits wallet directly"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Click to toggle")),
                event -> {
                    entry.setDropAsItem(!entry.isDropAsItem());
                    save();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    MobDropEntryEditGUI.open(plugin, player, currency, entry);
                })
        ).animate(() -> InventoryUtils.createItem(Material.CHEST, 1,
                entry.isDropAsItem()
                        ? Animation.fading("Drop Mode: Note", GREEN, LIME)
                        : Animation.fading("Drop Mode: Wallet", GOLD, ORANGE),
                entry.isDropAsItem()
                        ? Translate.miniMessage("<green>⬆ Physical note drops at mob location")
                        : Translate.miniMessage("<yellow>⬆ Credits wallet directly"),
                Translate.miniMessage("<dark_gray>▸ <gray>Click to toggle"))));

        buttons.add(((ClickEventButton) new ClickEventButton(22,
                InventoryUtils.createItem(Material.BARRIER, 1,
                        Translate.miniMessage("<red><bold>Delete Entry"),
                        Translate.miniMessage("<gray>Removes this mob drop entry")),
                event -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    currency.getMobDropManager().getMobDropEntries().remove(entry);
                    save();
                    MobDropListGUI.open(plugin, player, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.BARRIER, 1,
                Animation.fading("✖ Delete Entry", RED, RED2),
                Translate.miniMessage("<gray>Removes this mob drop entry"))));

        buttons.add(new ClickEventButton(26,
                InventoryUtils.createItem(Material.ARROW, 1,
                        Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    MobDropListGUI.open(plugin, player, currency);
                }));

        Component title = Translate.miniMessage("<dark_gray>✦ Drop Entry: <yellow>" + currency.getName());
        this.update(buttons, title, 27);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}

    private void save() {
        plugin.getStorageManager().saveAllCurrencies();
    }
}
