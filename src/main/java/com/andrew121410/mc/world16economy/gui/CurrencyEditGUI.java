package com.andrew121410.mc.world16economy.gui;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class CurrencyEditGUI extends GUIWindow {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor RED    = TextColor.color(0xFF1744);
    private static final TextColor RED2   = TextColor.color(0xFF6D00);

    private final World16Economy plugin;
    private final Currency currency;
    private final Player player;

    public CurrencyEditGUI(World16Economy plugin, Player player, Currency currency) {
        this.plugin = plugin;
        this.player = player;
        this.currency = currency;
    }

    public static void open(World16Economy plugin, Player player, Currency currency) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new CurrencyEditGUI(plugin, player, currency).open(player);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();

        // Row 1: text fields
        buttons.add(((ChatResponseButton) new ChatResponseButton(10,
                InventoryUtils.createItem(Material.NAME_TAG, 1,
                        Translate.miniMessage("<white><bold>Name"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getName()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Click to change")),
                (Component) null, (Component) null,
                (p, input) -> {
                    currency.setName(input.trim().toLowerCase().replace(" ", "-"));
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.NAME_TAG, 1,
                Animation.fading("Name", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getName()),
                Translate.miniMessage("<dark_gray>▸ <gray>Click to change"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(12,
                InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                        Translate.miniMessage("<white><bold>Symbol"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getSymbol()),
                        Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>$")),
                (Component) null, (Component) null,
                (p, input) -> {
                    currency.setSymbol(input.trim());
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                Animation.fading("Symbol", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getSymbol()),
                Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>$"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(14,
                InventoryUtils.createItem(Material.PAPER, 1,
                        Translate.miniMessage("<white><bold>Singular Name"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getCurrencyNameSingular()),
                        Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>Dollar")),
                (Component) null, (Component) null,
                (p, input) -> {
                    currency.setCurrencyNameSingular(input.trim());
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.PAPER, 1,
                Animation.fading("Singular Name", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getCurrencyNameSingular()),
                Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>Dollar"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(16,
                InventoryUtils.createItem(Material.BOOKSHELF, 1,
                        Translate.miniMessage("<white><bold>Plural Name"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getCurrencyNamePlural()),
                        Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>Dollars")),
                (Component) null, (Component) null,
                (p, input) -> {
                    currency.setCurrencyNamePlural(input.trim());
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.BOOKSHELF, 1,
                Animation.fading("Plural Name", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getCurrencyNamePlural()),
                Translate.miniMessage("<dark_gray>▸ <gray>e.g. <white>Dollars"))));

        // Row 2: style/value fields
        buttons.add(((ChatResponseButton) new ChatResponseButton(19,
                InventoryUtils.createItem(Material.LIME_DYE, 1,
                        Translate.miniMessage("<white><bold>Color"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getColor()),
                        Translate.miniMessage("<dark_gray>▸ <gray>MiniMessage tag, e.g. <gold><gold>")),
                (Component) null, (Component) null,
                (p, input) -> {
                    currency.setColor(input.trim());
                    save();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.LIME_DYE, 1,
                Animation.wave("Color", TextColor.color(0xFF0000), TextColor.color(0xFF7F00),
                        TextColor.color(0xFFFF00), TextColor.color(0x00FF00),
                        TextColor.color(0x0000FF), TextColor.color(0x8B00FF)),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getColor()),
                Translate.miniMessage("<dark_gray>▸ <gray>MiniMessage tag, e.g. <gold><gold>"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(21,
                InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                        Translate.miniMessage("<white><bold>Default Balance"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getDefaultMoney()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Starting balance for new players")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        currency.setDefaultMoney((long) Double.parseDouble(input.trim()));
                        save();
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid number."));
                    }
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                Animation.fading("Default Balance", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getDefaultMoney()),
                Translate.miniMessage("<dark_gray>▸ <gray>Starting balance for new players"))));

        buttons.add(((ChatResponseButton) new ChatResponseButton(23,
                InventoryUtils.createItem(currency.getItemMaterial(), 1,
                        Translate.miniMessage("<white><bold>Icon Material"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getItemMaterial().name()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Type a Bukkit Material name")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        currency.setItemMaterial(org.bukkit.Material.valueOf(input.trim().toUpperCase()));
                        save();
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (IllegalArgumentException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Unknown material: <white>" + input.trim()));
                    }
                    CurrencyEditGUI.open(plugin, p, currency);
                })
        ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                Animation.fading("Icon Material", AQUA, BLUE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + currency.getItemMaterial().name()),
                Translate.miniMessage("<dark_gray>▸ <gray>Type a Bukkit Material name"))));

        boolean isDefault = currency.getUuid().equals(plugin.getCurrenciesManager().getDefaultCurrencyUUID());
        buttons.add(((ClickEventButton) new ClickEventButton(25,
                InventoryUtils.createItem(Material.BEACON, 1,
                        Translate.miniMessage("<white><bold>Set as Default"),
                        isDefault
                                ? Translate.miniMessage("<green>✔ Currently the default currency")
                                : Translate.miniMessage("<gray>Click to make this the default")),
                event -> {
                    plugin.getCurrenciesManager().setDefaultCurrencyUUID(currency.getUuid());
                    save();
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    CurrencyEditGUI.open(plugin, player, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.BEACON, 1,
                isDefault
                        ? Animation.fading("✔ Default Currency", GREEN, LIME)
                        : Animation.fading("Set as Default", GOLD, ORANGE),
                isDefault
                        ? Translate.miniMessage("<green>✔ Currently the default currency")
                        : Translate.miniMessage("<gray>Click to make this the default"))));

        // Row 4: actions
        buttons.add(((ClickEventButton) new ClickEventButton(37,
                InventoryUtils.createItem(Material.ZOMBIE_HEAD, 1,
                        Translate.miniMessage("<white><bold>Mob Drops"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Entries: <yellow>" + currency.getMobDropManager().getMobDropEntries().size()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Click to configure")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    MobDropListGUI.open(plugin, player, currency);
                })
        ).animate(() -> InventoryUtils.createItem(Material.ZOMBIE_HEAD, 1,
                Animation.wave("Mob Drops", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Entries: <yellow>" + currency.getMobDropManager().getMobDropEntries().size()),
                Translate.miniMessage("<dark_gray>▸ <gray>Click to configure"))));

        buttons.add(((ClickEventButton) new ClickEventButton(43,
                InventoryUtils.createItem(Material.BARRIER, 1,
                        Translate.miniMessage("<red><bold>Delete Currency"),
                        Translate.miniMessage("<gray>This cannot be undone")),
                event -> {
                    if (plugin.getCurrenciesManager().getCurrenciesByUUID().size() <= 1) {
                        player.sendMessage(Translate.miniMessage("<red>✖ Cannot delete the only currency."));
                        return;
                    }

                    if (currency.getUuid().equals(plugin.getCurrenciesManager().getDefaultCurrencyUUID())) {
                        player.sendMessage(Translate.miniMessage("<red>✖ Cannot delete the default currency. Set a different currency as default first."));
                        return;
                    }

                    // Block deletion if anything still references this currency
                    long walletCount = plugin.getWalletManager().countWalletsWithBalance(currency.getUuid());
                    long bankCount = plugin.getBankManager().countAccountsWithBalance(currency.getUuid());
                    long noteCount = plugin.getNoteManager().countNotesByCurrency(currency.getUuid());
                    long payrollCount = plugin.getBankManager().countPayrollsUsing(currency.getUuid());

                    boolean blocked = walletCount > 0 || bankCount > 0 || noteCount > 0 || payrollCount > 0;
                    if (blocked) {
                        player.sendMessage(Translate.miniMessage("<red>✖ Cannot delete <white>" + currency.getName() + " <red>— it is still in use:"));
                        if (walletCount > 0) player.sendMessage(Translate.miniMessage("<dark_gray>▸ <white>" + walletCount + " <gray>player wallet(s) have a non-zero balance"));
                        if (bankCount > 0) player.sendMessage(Translate.miniMessage("<dark_gray>▸ <white>" + bankCount + " <gray>bank account(s) hold a balance"));
                        if (noteCount > 0) player.sendMessage(Translate.miniMessage("<dark_gray>▸ <white>" + noteCount + " <gray>outstanding note(s) exist"));
                        if (payrollCount > 0) player.sendMessage(Translate.miniMessage("<dark_gray>▸ <white>" + payrollCount + " <gray>payroll(s) are configured to pay in this currency"));
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    plugin.getCurrenciesManager().removeCurrency(currency);
                    save();
                    CurrencyListGUI.open(plugin, player);
                })
        ).animate(() -> InventoryUtils.createItem(Material.BARRIER, 1,
                Animation.fading("✖ Delete Currency", RED, RED2),
                Translate.miniMessage("<gray>This cannot be undone"))));

        buttons.add(new ClickEventButton(49,
                InventoryUtils.createItem(Material.ARROW, 1,
                        Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    CurrencyListGUI.open(plugin, player);
                }));

        Component title = Translate.miniMessage("<dark_gray>✦ Edit: <yellow>" + currency.getName());
        this.update(buttons, title, 54);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}

    private void save() {
        plugin.getStorageManager().saveAllCurrencies();
    }
}
