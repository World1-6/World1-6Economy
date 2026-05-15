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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class BankAccountGUI extends GUIWindow {

    private static final TextColor GOLD     = TextColor.color(0xFFD700);
    private static final TextColor ORANGE   = TextColor.color(0xFF8C00);
    private static final TextColor GREEN    = TextColor.color(0x00C853);
    private static final TextColor LIME     = TextColor.color(0x69FF47);
    private static final TextColor AQUA     = TextColor.color(0x00E5FF);
    private static final TextColor BLUE     = TextColor.color(0x2979FF);
    private static final TextColor RED      = TextColor.color(0xFF1744);
    private static final TextColor RED2     = TextColor.color(0xFF6D00);
    private static final TextColor PURPLE   = TextColor.color(0xAA00FF);
    private static final TextColor PURPLE_LT= TextColor.color(0xE040FB);

    private final World16Economy plugin;
    private final Player player;
    private final BankAccount account;
    private final BankRole role;

    public BankAccountGUI(World16Economy plugin, Player player, BankAccount account) {
        this.plugin = plugin;
        this.player = player;
        this.account = account;
        this.role = account.getRoleOf(player.getUniqueId());
    }

    public static void open(World16Economy plugin, Player player, BankAccount account) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new BankAccountGUI(plugin, player, account).open(player);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();

        BankTier tier = plugin.getBankManager().getTier(account.getTierLevel());
        String tierName = tier != null ? tier.getDisplayName() : "Tier " + account.getTierLevel();
        boolean isBusiness = account.isBusiness();

        // --- Row 1: Balance displays per currency (slots 10–16) ---
        int balSlot = 10;
        for (var entry : account.getBalances().entrySet()) {
            if (balSlot > 16) break;
            Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(entry.getKey());
            if (currency == null) continue;
            double balance = entry.getValue();
            String formatted = String.format("%,.2f", balance);
            final int slot = balSlot++;

            buttons.add(((NoEventButton) new NoEventButton(slot, InventoryUtils.createItem(
                    currency.getItemMaterial(), 1,
                    Translate.miniMessage("<bold>" + currency.getColor() + formatted + " " + currency.getCurrencyNamePlural())
                            .decoration(TextDecoration.ITALIC, false),
                    Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + currency.getName()),
                    tier != null && tier.hasBalanceLimit()
                            ? Translate.miniMessage("<dark_gray>▸ <gray>Limit: <white>" + String.format("%,.0f", tier.getBalanceLimit()))
                            : Translate.miniMessage("<dark_gray>▸ <gray>Limit: <white>Unlimited")))
            ).animate(() -> {
                Currency c = plugin.getCurrenciesManager().getCurrencyByUUID(entry.getKey());
                if (c == null) return InventoryUtils.createItem(Material.BARRIER, 1, Translate.miniMessage("<red>Unknown"));
                double b = account.getBalance(entry.getKey());
                String fmt = String.format("%,.2f", b);
                return InventoryUtils.createItem(c.getItemMaterial(), 1,
                        Animation.wave(fmt + " " + c.getCurrencyNamePlural(), GOLD, ORANGE)
                                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                        Translate.miniMessage("<dark_gray>▸ <gray>Currency: <white>" + c.getName()));
            }));
        }

        // --- Row 2: Actions ---

        // Deposit (slot 19) — available to OWNER, MANAGER, EMPLOYEE
        if (role.canDeposit()) {
            buttons.add(((ClickEventButton) new ClickEventButton(19,
                    InventoryUtils.createItem(Material.LIME_STAINED_GLASS_PANE, 1,
                            Translate.miniMessage("<green><bold>Deposit"),
                            Translate.miniMessage("<gray>Add funds to this account")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankDepositWithdrawGUI.openCurrencyPicker(plugin, player, account, true);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.LIME_STAINED_GLASS_PANE, 1,
                    Animation.wave("⬆ Deposit", GREEN, LIME),
                    Translate.miniMessage("<gray>Add funds to this account"))));
        }

        // Withdraw (slot 21) — OWNER, MANAGER
        if (role.canWithdraw()) {
            buttons.add(((ClickEventButton) new ClickEventButton(21,
                    InventoryUtils.createItem(Material.RED_STAINED_GLASS_PANE, 1,
                            Translate.miniMessage("<red><bold>Withdraw"),
                            Translate.miniMessage("<gray>Remove funds from this account")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankDepositWithdrawGUI.openCurrencyPicker(plugin, player, account, false);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.RED_STAINED_GLASS_PANE, 1,
                    Animation.wave("⬇ Withdraw", RED, RED2),
                    Translate.miniMessage("<gray>Remove funds from this account"))));
        }

        // Transaction history (slot 23) — OWNER, MANAGER, VIEWER
        if (role.canViewHistory()) {
            buttons.add(((ClickEventButton) new ClickEventButton(23,
                    InventoryUtils.createItem(Material.BOOK, 1,
                            Translate.miniMessage("<white><bold>Transaction History"),
                            Translate.miniMessage("<gray>View recent transactions")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankTransactionHistoryGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.BOOK, 1,
                    Animation.fading("Transaction History", AQUA, BLUE),
                    Translate.miniMessage("<gray>View recent transactions"))));
        }

        // Members (slot 25) — business only, OWNER or MANAGER
        if (isBusiness && role.canManageMembers()) {
            buttons.add(((ClickEventButton) new ClickEventButton(25,
                    InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                            Translate.miniMessage("<white><bold>Members"),
                            Translate.miniMessage("<dark_gray>▸ <gray>Count: <white>" + account.getMembers().size()),
                            Translate.miniMessage("<gray>Manage employees & roles")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankMembersGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                    Animation.fading("Members", PURPLE, PURPLE_LT),
                    Translate.miniMessage("<dark_gray>▸ <gray>Count: <white>" + account.getMembers().size()),
                    Translate.miniMessage("<gray>Manage employees & roles"))));
        }

        // --- Row 3: Owner-only actions ---

        // Payroll (slot 37) — business + OWNER
        if (isBusiness && role.canManagePayroll()) {
            BankScheduledPayroll payroll = account.getScheduledPayroll();
            buttons.add(((ClickEventButton) new ClickEventButton(37,
                    InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                            Translate.miniMessage("<white><bold>Payroll"),
                            payroll != null
                                    ? Translate.miniMessage("<green>✔ Scheduled every <white>" + formatInterval(payroll.getIntervalMs()))
                                    : Translate.miniMessage("<gray>No schedule configured"),
                            Translate.miniMessage("<gray>Click to manage")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankPayrollGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                    Animation.wave("Payroll", GOLD, ORANGE),
                    account.getScheduledPayroll() != null
                            ? Translate.miniMessage("<green>✔ Scheduled every <white>" + formatInterval(account.getScheduledPayroll().getIntervalMs()))
                            : Translate.miniMessage("<gray>No schedule configured"),
                    Translate.miniMessage("<gray>Click to manage"))));
        }

        // Upgrade (slot 39) — OWNER
        if (role.canUpgrade() && plugin.getBankManager().hasTier(account.getTierLevel() + 1)) {
            BankTier next = plugin.getBankManager().getNextTier(account.getTierLevel());
            buttons.add(((ClickEventButton) new ClickEventButton(39,
                    InventoryUtils.createItem(Material.NETHER_STAR, 1,
                            Translate.miniMessage("<white><bold>Upgrade"),
                            Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + tierName),
                            next != null ? Translate.miniMessage("<dark_gray>▸ <gray>Next: <white>" + next.getDisplayName()
                                    + " <gray>(<yellow>" + String.format("%,.0f", next.getCreationCost()) + "<gray>)") : Component.empty()),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankUpgradeGUI.open(plugin, player, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.NETHER_STAR, 1,
                    Animation.wave("⬆ Upgrade", GOLD, ORANGE),
                    Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + tierName))));
        }

        // Rename (slot 41) — OWNER
        if (role == BankRole.OWNER) {
            buttons.add(((ChatResponseButton) new ChatResponseButton(41,
                    InventoryUtils.createItem(Material.NAME_TAG, 1,
                            Translate.miniMessage("<white><bold>Rename"),
                            Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + account.getName())),
                    (Component) null, (Component) null,
                    (p, input) -> {
                        String name = input.trim();
                        if (name.isEmpty()) {
                            p.sendMessage(Translate.miniMessage("<red>✖ Name cannot be empty."));
                        } else {
                            account.setName(name);
                            plugin.getBankManager().saveAccount(account);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        }
                        BankAccountGUI.open(plugin, p, account);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.NAME_TAG, 1,
                    Animation.fading("Rename", AQUA, BLUE),
                    Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + account.getName()))));
        }

        // Delete (slot 43) — OWNER
        if (role.canDelete()) {
            buttons.add(((ClickEventButton) new ClickEventButton(43,
                    InventoryUtils.createItem(Material.BARRIER, 1,
                            Translate.miniMessage("<red><bold>Delete Account"),
                            Translate.miniMessage("<gray>This cannot be undone")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        plugin.getBankManager().deleteAccount(account);
                        BankListGUI.open(plugin, player);
                    })
            ).animate(() -> InventoryUtils.createItem(Material.BARRIER, 1,
                    Animation.fading("✖ Delete Account", RED, RED2),
                    Translate.miniMessage("<gray>This cannot be undone"))));
        }

        // Transfer (slot 45) — OWNER and MANAGER, only if player has other accounts
        if (role.canWithdraw()) {
            List<com.andrew121410.mc.world16economy.bank.BankAccount> otherAccounts = plugin.getBankManager()
                    .getAccountsForPlayer(player.getUniqueId()).stream()
                    .filter(a -> !a.getAccountUUID().equals(account.getAccountUUID()))
                    .toList();
            if (!otherAccounts.isEmpty()) {
                buttons.add(((ClickEventButton) new ClickEventButton(45,
                        InventoryUtils.createItem(Material.HOPPER, 1,
                                Translate.miniMessage("<white><bold>Transfer"),
                                Translate.miniMessage("<gray>Send funds to another account")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            BankTransferGUI.open(plugin, player, account);
                        })
                ).animate(() -> InventoryUtils.createItem(Material.HOPPER, 1,
                        Animation.wave("⇄ Transfer", AQUA, BLUE),
                        Translate.miniMessage("<gray>Send funds to another account"))));
            }
        }

        // Payroll destination (slot 47) — EMPLOYEE and MANAGER can set where their wage goes
        if (isBusiness && (role == BankRole.EMPLOYEE || role == BankRole.MANAGER)) {
            BankMember self = account.getMembers().get(player.getUniqueId());
            String destName = "<white>Personal Wallet";
            if (self != null && self.getPayrollDestinationAccountUUID() != null) {
                BankAccount dest = plugin.getBankManager().getAccount(self.getPayrollDestinationAccountUUID());
                destName = dest != null ? "<white>" + dest.getName() : "<red>⚠ Deleted";
            }
            final String destDisplay = destName;
            buttons.add(((ClickEventButton) new ClickEventButton(47,
                    InventoryUtils.createItem(Material.ENDER_CHEST, 1,
                            Translate.miniMessage("<white><bold>My Payroll Destination"),
                            Translate.miniMessage("<dark_gray>▸ <gray>Goes to: " + destDisplay),
                            Translate.miniMessage("<gray>Click to change")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankPayrollDestinationGUI.open(plugin, player, account);
                    })
            ).animate(() -> {
                BankMember s = account.getMembers().get(player.getUniqueId());
                String dn = "<white>Personal Wallet";
                if (s != null && s.getPayrollDestinationAccountUUID() != null) {
                    BankAccount d = plugin.getBankManager().getAccount(s.getPayrollDestinationAccountUUID());
                    dn = d != null ? "<white>" + d.getName() : "<red>⚠ Deleted";
                }
                return InventoryUtils.createItem(Material.ENDER_CHEST, 1,
                        Animation.fading("My Payroll Destination", AQUA, BLUE),
                        Translate.miniMessage("<dark_gray>▸ <gray>Goes to: " + dn),
                        Translate.miniMessage("<gray>Click to change"));
            }));
        }

        // Back (slot 49)
        buttons.add(new ClickEventButton(49,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankListGUI.open(plugin, player);
                }));

        this.update(buttons,
                Translate.miniMessage("<dark_gray>✦ " + (isBusiness ? "<light_purple>" : "<aqua>") + account.getName()),
                54);
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
