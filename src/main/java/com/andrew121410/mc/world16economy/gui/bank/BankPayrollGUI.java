package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.*;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankPayrollGUI extends GUIWindow {

    private static final TextColor GOLD    = TextColor.color(0xFFD700);
    private static final TextColor ORANGE  = TextColor.color(0xFF8C00);
    private static final TextColor GREEN   = TextColor.color(0x00C853);
    private static final TextColor LIME    = TextColor.color(0x69FF47);
    private static final TextColor AQUA    = TextColor.color(0x00E5FF);
    private static final TextColor BLUE    = TextColor.color(0x2979FF);
    private static final TextColor RED     = TextColor.color(0xFF1744);
    private static final TextColor RED2    = TextColor.color(0xFF6D00);

    private final World16Economy plugin;
    private final Player player;
    private final BankAccount account;

    public BankPayrollGUI(World16Economy plugin, Player player, BankAccount account) {
        this.plugin = plugin;
        this.player = player;
        this.account = account;
    }

    public static void open(World16Economy plugin, Player player, BankAccount account) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new BankPayrollGUI(plugin, player, account).open(player);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();
        BankScheduledPayroll payroll = account.getScheduledPayroll();

        // --- Employee roster (slots 0–8, up to 9 shown) ---
        int empSlot = 0;
        for (BankMember member : account.getMembers().values()) {
            if (empSlot >= 9) break;
            if (member.getRole() == BankRole.OWNER || member.getRole() == BankRole.VIEWER) continue;

            OfflinePlayer op = Bukkit.getOfflinePlayer(member.getPlayerUUID());
            String name = op.getName() != null ? op.getName() : member.getPlayerUUID().toString().substring(0, 8);

            String destLine;
            if (member.getPayrollDestinationAccountUUID() == null) {
                destLine = "<gray>→ <white>Personal Wallet";
            } else {
                BankAccount dest = plugin.getBankManager().getAccount(member.getPayrollDestinationAccountUUID());
                if (dest != null) {
                    destLine = "<gray>→ <white>" + dest.getName();
                } else {
                    destLine = "<red>⚠ Destination account deleted";
                }
            }

            buttons.add(new NoEventButton(empSlot++, InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                    Translate.miniMessage("<white><bold>" + name).decoration(TextDecoration.ITALIC, false),
                    Translate.miniMessage("<dark_gray>▸ <gray>Role: <white>" + member.getRole().name()),
                    Translate.miniMessage("<dark_gray>▸ <gray>Wage: <yellow>" + String.format("%,.2f", member.getWage())),
                    Translate.miniMessage("<dark_gray>▸ " + destLine))));
        }

        // --- Schedule config (row 2) ---

        // Set interval
        buttons.add(((ChatResponseButton) new ChatResponseButton(19,
                InventoryUtils.createItem(Material.CLOCK, 1,
                        Translate.miniMessage("<white><bold>Set Interval"),
                        payroll != null
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + formatInterval(payroll.getIntervalMs()))
                                : Translate.miniMessage("<gray>No schedule set"),
                        Translate.miniMessage("<gray>Type in minutes, e.g. <white>60 <gray>for 1 hour")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        long minutes = Long.parseLong(input.trim());
                        if (minutes <= 0) throw new NumberFormatException();
                        long intervalMs = minutes * 60_000L;

                        if (payroll == null) {
                            // Need a currency — use default
                            UUID defaultCurrency = plugin.getCurrenciesManager().getDefaultCurrencyUUID();
                            BankScheduledPayroll newPayroll = new BankScheduledPayroll(
                                    account.getAccountUUID(), defaultCurrency, intervalMs, 0);
                            account.setScheduledPayroll(newPayroll);
                        } else {
                            payroll.setIntervalMs(intervalMs);
                        }
                        plugin.getBankManager().savePayroll(account);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        p.sendMessage(Translate.miniMessage("<green>✔ Payroll interval set to <white>" + minutes + " minutes<green>."));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid number. Enter minutes as a whole number."));
                    }
                    BankPayrollGUI.open(plugin, p, account);
                })
        ).animate(() -> InventoryUtils.createItem(Material.CLOCK, 1,
                Animation.fading("Set Interval", AQUA, BLUE),
                account.getScheduledPayroll() != null
                        ? Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + formatInterval(account.getScheduledPayroll().getIntervalMs()))
                        : Translate.miniMessage("<gray>No schedule set"),
                Translate.miniMessage("<gray>Type in minutes, e.g. <white>60"))));

        // Set payroll currency
        buttons.add(((ChatResponseButton) new ChatResponseButton(21,
                buildCurrencyItem(payroll),
                (Component) null, (Component) null,
                (p, input) -> {
                    Currency found = plugin.getCurrenciesManager().getCurrenciesByUUID().values().stream()
                            .filter(c -> c.getName().equalsIgnoreCase(input.trim())
                                    || c.getCurrencyNameSingular().equalsIgnoreCase(input.trim())
                                    || c.getCurrencyNamePlural().equalsIgnoreCase(input.trim()))
                            .findFirst().orElse(null);
                    if (found == null) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Unknown currency: <white>" + input.trim()));
                        BankPayrollGUI.open(plugin, p, account);
                        return;
                    }
                    if (account.getScheduledPayroll() == null) {
                        BankScheduledPayroll newPayroll = new BankScheduledPayroll(
                                account.getAccountUUID(), found.getUuid(), 60 * 60_000L, 0);
                        account.setScheduledPayroll(newPayroll);
                    } else {
                        account.getScheduledPayroll().setCurrencyUUID(found.getUuid());
                    }
                    plugin.getBankManager().savePayroll(account);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    BankPayrollGUI.open(plugin, p, account);
                })
        ).animate(() -> {
            BankScheduledPayroll p2 = account.getScheduledPayroll();
            if (p2 == null) return InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                    Animation.fading("Payroll Currency", GOLD, ORANGE),
                    Translate.miniMessage("<gray>No schedule set yet"));
            Currency c = plugin.getCurrenciesManager().getCurrencyByUUID(p2.getCurrencyUUID());
            return InventoryUtils.createItem(c != null ? c.getItemMaterial() : Material.GOLD_NUGGET, 1,
                    Animation.fading("Payroll Currency", GOLD, ORANGE),
                    Translate.miniMessage("<dark_gray>▸ <gray>Current: " + (c != null ? c.getColor() + c.getName() : "<red>Unknown")));
        }));

        // Run now
        buttons.add(((ClickEventButton) new ClickEventButton(23,
                InventoryUtils.createItem(Material.EMERALD, 1,
                        Translate.miniMessage("<green><bold>Run Payroll Now"),
                        Translate.miniMessage("<gray>Immediately pays all employees")),
                event -> {
                    if (account.getScheduledPayroll() == null) {
                        player.sendMessage(Translate.miniMessage("<red>✖ No payroll schedule configured yet."));
                        return;
                    }
                    plugin.getBankManager().runPayroll(account, player.getUniqueId());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    player.sendMessage(Translate.miniMessage("<green>✔ Payroll run complete."));
                    BankPayrollGUI.open(plugin, player, account);
                })
        ).animate(() -> InventoryUtils.createItem(Material.EMERALD, 1,
                Animation.wave("▶ Run Payroll Now", GREEN, LIME),
                Translate.miniMessage("<gray>Immediately pays all employees"))));

        // Online only toggle
        if (payroll != null) {
            boolean onlineOnly = payroll.isOnlineOnly();
            buttons.add(((ClickEventButton) new ClickEventButton(27,
                    InventoryUtils.createItem(onlineOnly ? Material.LIME_DYE : Material.GRAY_DYE, 1,
                            Translate.miniMessage("<white><bold>Online Only"),
                            Translate.miniMessage(onlineOnly ? "<green>✔ Enabled — offline players are skipped" : "<gray>✘ Disabled — pays everyone"),
                            Translate.miniMessage("<gray>Click to toggle")),
                    event -> {
                        payroll.setOnlineOnly(!payroll.isOnlineOnly());
                        plugin.getBankManager().savePayroll(account);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankPayrollGUI.open(plugin, player, account);
                    })
            ).animate(() -> {
                BankScheduledPayroll p2 = account.getScheduledPayroll();
                boolean oo = p2 != null && p2.isOnlineOnly();
                return InventoryUtils.createItem(oo ? Material.LIME_DYE : Material.GRAY_DYE, 1,
                        Animation.fading("Online Only", oo ? GREEN : TextColor.color(0x9E9E9E), oo ? LIME : TextColor.color(0x616161)),
                        Translate.miniMessage(oo ? "<green>✔ Offline players are skipped" : "<gray>✘ Pays everyone regardless"),
                        Translate.miniMessage("<gray>Click to toggle"));
            }));
        }

        // Remove schedule
        if (payroll != null) {
            buttons.add(((ClickEventButton) new ClickEventButton(25,
                    InventoryUtils.createItem(Material.BARRIER, 1,
                            Translate.miniMessage("<red><bold>Remove Schedule"),
                            Translate.miniMessage("<gray>Stops automatic payroll")),
                    event -> {
                        account.setScheduledPayroll(null);
                        plugin.getBankManager().savePayroll(account);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        BankPayrollGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.BARRIER, 1,
                    Animation.fading("✖ Remove Schedule", RED, RED2),
                    Translate.miniMessage("<gray>Stops automatic payroll"))));
        }

        // Back
        buttons.add(new ClickEventButton(49,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, account);
                }));

        this.update(buttons, Translate.miniMessage("<dark_gray>✦ Payroll: <gold>" + account.getName()), 54);
    }

    private org.bukkit.inventory.ItemStack buildCurrencyItem(BankScheduledPayroll payroll) {
        if (payroll == null) {
            return InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                    Translate.miniMessage("<white><bold>Payroll Currency"),
                    Translate.miniMessage("<gray>No schedule set yet"),
                    Translate.miniMessage("<gray>Type a currency name in chat"));
        }
        Currency c = plugin.getCurrenciesManager().getCurrencyByUUID(payroll.getCurrencyUUID());
        return InventoryUtils.createItem(c != null ? c.getItemMaterial() : Material.GOLD_NUGGET, 1,
                Translate.miniMessage("<white><bold>Payroll Currency"),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: " + (c != null ? c.getColor() + c.getName() : "<red>Unknown")),
                Translate.miniMessage("<gray>Type a currency name in chat"));
    }

    private static String formatInterval(long ms) {
        long seconds = ms / 1000;
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h";
        return (hours / 24) + "d";
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
