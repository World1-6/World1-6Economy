package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankMember;
import com.andrew121410.mc.world16economy.bank.BankRole;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class BankMemberEditGUI extends GUIWindow {

    private static final TextColor GOLD    = TextColor.color(0xFFD700);
    private static final TextColor ORANGE  = TextColor.color(0xFF8C00);
    private static final TextColor AQUA    = TextColor.color(0x00E5FF);
    private static final TextColor BLUE    = TextColor.color(0x2979FF);
    private static final TextColor RED     = TextColor.color(0xFF1744);
    private static final TextColor RED2    = TextColor.color(0xFF6D00);
    private static final TextColor PURPLE  = TextColor.color(0xAA00FF);
    private static final TextColor PURPLE_LT = TextColor.color(0xE040FB);

    private final World16Economy plugin;
    private final Player player;
    private final BankAccount account;
    private final BankMember member;

    public BankMemberEditGUI(World16Economy plugin, Player player, BankAccount account, BankMember member) {
        this.plugin = plugin;
        this.player = player;
        this.account = account;
        this.member = member;
    }

    public static void open(World16Economy plugin, Player player, BankAccount account, BankMember member) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        new BankMemberEditGUI(plugin, player, account, member).open(player);
    }

    @Override
    public void onCreate(Player player) {
        List<AbstractGUIButton> buttons = new ArrayList<>();

        OfflinePlayer op = Bukkit.getOfflinePlayer(member.getPlayerUUID());
        String memberName = op.getName() != null ? op.getName() : member.getPlayerUUID().toString().substring(0, 8);

        // Role cycle
        BankRole[] roles = { BankRole.MANAGER, BankRole.EMPLOYEE, BankRole.VIEWER };
        buttons.add(((ClickEventButton) new ClickEventButton(11,
                InventoryUtils.createItem(Material.IRON_SWORD, 1,
                        Translate.miniMessage("<white><bold>Role"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <white>" + member.getRole().name()),
                        Translate.miniMessage("<gray>Click to cycle: Manager → Employee → Viewer")),
                event -> {
                    BankRole current = member.getRole();
                    BankRole next = current == BankRole.MANAGER ? BankRole.EMPLOYEE
                            : current == BankRole.EMPLOYEE ? BankRole.VIEWER : BankRole.MANAGER;
                    member.setRole(next);
                    plugin.getBankManager().saveMember(account, member);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankMemberEditGUI.open(plugin, player, account, member);
                })
        ).animate(() -> InventoryUtils.createItem(Material.IRON_SWORD, 1,
                Animation.fading("Role: " + member.getRole().name(), PURPLE, PURPLE_LT),
                Translate.miniMessage("<gray>Click to cycle: Manager → Employee → Viewer"))));

        // Wage
        buttons.add(((ChatResponseButton) new ChatResponseButton(13,
                InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                        Translate.miniMessage("<white><bold>Wage"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + String.format("%,.2f", member.getWage())),
                        Translate.miniMessage("<gray>Amount paid per payroll run")),
                (Component) null, (Component) null,
                (p, input) -> {
                    try {
                        double wage = Double.parseDouble(input.trim());
                        if (wage < 0) throw new NumberFormatException();
                        member.setWage(wage);
                        plugin.getBankManager().saveMember(account, member);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } catch (NumberFormatException e) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Invalid amount."));
                    }
                    BankMemberEditGUI.open(plugin, p, account, member);
                })
        ).animate(() -> InventoryUtils.createItem(Material.GOLD_NUGGET, 1,
                Animation.fading("Wage", GOLD, ORANGE),
                Translate.miniMessage("<dark_gray>▸ <gray>Current: <yellow>" + String.format("%,.2f", member.getWage())),
                Translate.miniMessage("<gray>Amount paid per payroll run"))));

        // Payroll destination info
        String destDisplay;
        if (member.getPayrollDestinationAccountUUID() == null) {
            destDisplay = "<white>Personal Wallet";
        } else {
            BankAccount dest = plugin.getBankManager().getAccount(member.getPayrollDestinationAccountUUID());
            destDisplay = dest != null ? "<white>" + dest.getName() : "<red>⚠ Deleted";
        }
        buttons.add(new com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton(15,
                InventoryUtils.createItem(Material.ENDER_CHEST, 1,
                        Translate.miniMessage("<white><bold>Payroll Destination"),
                        Translate.miniMessage("<dark_gray>▸ <gray>Current: " + destDisplay),
                        Translate.miniMessage("<gray>Employee can change this themselves"))));

        // Kick
        buttons.add(((ClickEventButton) new ClickEventButton(22,
                InventoryUtils.createItem(Material.BARRIER, 1,
                        Translate.miniMessage("<red><bold>Remove Member"),
                        Translate.miniMessage("<gray>Removes " + memberName + " from this account")),
                event -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    plugin.getBankManager().deleteMember(account, member.getPlayerUUID());
                    BankMembersGUI.open(plugin, player, account);
                })
        ).animate(() -> InventoryUtils.createItem(Material.BARRIER, 1,
                Animation.fading("✖ Remove " + memberName, RED, RED2),
                Translate.miniMessage("<gray>Removes this member from the account"))));

        // Back
        buttons.add(new ClickEventButton(26,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankMembersGUI.open(plugin, player, account);
                }));

        this.update(buttons, Translate.miniMessage("<dark_gray>✦ Edit: <light_purple>" + memberName), 27);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
