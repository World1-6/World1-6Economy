package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankMember;
import com.andrew121410.mc.world16economy.bank.BankRole;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankMembersGUI {

    private static final TextColor GOLD     = TextColor.color(0xFFD700);
    private static final TextColor ORANGE   = TextColor.color(0xFF8C00);
    private static final TextColor GREEN    = TextColor.color(0x00C853);
    private static final TextColor LIME     = TextColor.color(0x69FF47);
    private static final TextColor RED      = TextColor.color(0xFF1744);
    private static final TextColor RED2     = TextColor.color(0xFF6D00);
    private static final TextColor PURPLE   = TextColor.color(0xAA00FF);
    private static final TextColor PURPLE_LT= TextColor.color(0xE040FB);

    public static void open(World16Economy plugin, Player player, BankAccount account) {
        List<CloneableGUIButton> buttons = new ArrayList<>();

        for (BankMember member : account.getMembers().values()) {
            if (member.getRole() == BankRole.OWNER) continue; // owner not editable from here

            OfflinePlayer op = Bukkit.getOfflinePlayer(member.getPlayerUUID());
            String name = op.getName() != null ? op.getName() : member.getPlayerUUID().toString().substring(0, 8);

            // Resolve payroll destination display
            String destLine;
            if (member.getPayrollDestinationAccountUUID() == null) {
                destLine = "<gray>Payroll → <white>Personal Wallet";
            } else {
                BankAccount dest = plugin.getBankManager().getAccount(member.getPayrollDestinationAccountUUID());
                if (dest != null) {
                    destLine = "<gray>Payroll → <white>" + dest.getName();
                } else {
                    destLine = "<red>⚠ Payroll destination deleted";
                }
            }

            final String finalDestLine = destLine;

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                            Translate.miniMessage("<bold><white>" + name).decoration(TextDecoration.ITALIC, false),
                            Translate.miniMessage("<dark_gray>▸ <gray>Role: <white>" + member.getRole().name()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Wage: <yellow>" + String.format("%,.2f", member.getWage())),
                            Translate.miniMessage("<dark_gray>▸ " + finalDestLine),
                            Component.empty(),
                            Translate.miniMessage("<yellow>⚙ Click to manage")),
                    event -> {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        BankMemberEditGUI.open(plugin, player, account, member);
                    })
            ).animate(() -> {
                String dl;
                if (member.getPayrollDestinationAccountUUID() == null) {
                    dl = "<gray>Payroll → <white>Personal Wallet";
                } else {
                    BankAccount dest = plugin.getBankManager().getAccount(member.getPayrollDestinationAccountUUID());
                    dl = dest != null ? "<gray>Payroll → <white>" + dest.getName() : "<red>⚠ Destination deleted";
                }
                return InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                        Animation.fading(name, PURPLE, PURPLE_LT)
                                .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                        Translate.miniMessage("<dark_gray>▸ <gray>Role: <white>" + member.getRole().name()),
                        Translate.miniMessage("<dark_gray>▸ <gray>Wage: <yellow>" + String.format("%,.2f", member.getWage())),
                        Translate.miniMessage("<dark_gray>▸ " + dl),
                        Component.empty(),
                        Translate.miniMessage("<yellow>⚙ Click to manage"));
            }));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Members: <light_purple>" + account.getName()), buttons);

        // Invite member button
        gui.getCustomBottomButtons().add(((ChatResponseButton) new ChatResponseButton(47,
                InventoryUtils.createItem(Material.EMERALD, 1,
                        Translate.miniMessage("<green><bold>Invite Member"),
                        Translate.miniMessage("<gray>Type the player's name in chat")),
                (Component) null, (Component) null,
                (p, input) -> {
                    OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(input.trim());
                    if (target == null) {
                        p.sendMessage(Translate.miniMessage("<red>✖ Player not found: <white>" + input.trim()));
                        BankMembersGUI.open(plugin, p, account);
                        return;
                    }
                    if (account.hasMember(target.getUniqueId())) {
                        p.sendMessage(Translate.miniMessage("<red>✖ That player is already a member."));
                        BankMembersGUI.open(plugin, p, account);
                        return;
                    }
                    BankMember newMember = new com.andrew121410.mc.world16economy.bank.BankMember(
                            target.getUniqueId(), BankRole.EMPLOYEE, 0, null);
                    account.getMembers().put(target.getUniqueId(), newMember);
                    plugin.getBankManager().saveMember(account, newMember);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    p.sendMessage(Translate.miniMessage("<green>✔ Invited <white>" + target.getName() + "<green> as Employee."));

                    Player online = Bukkit.getPlayer(target.getUniqueId());
                    if (online != null) {
                        online.sendMessage(Translate.miniMessage("<green>✔ You have been added to the bank account <white>"
                                + account.getName() + "<green> as an Employee."));
                    }
                    BankMembersGUI.open(plugin, p, account);
                })
        ).animate(() -> InventoryUtils.createItem(Material.EMERALD, 1,
                Animation.wave("✦ Invite Member", GREEN, LIME),
                Translate.miniMessage("<gray>Type the player's name in chat"))));

        // Back
        gui.getCustomBottomButtons().add(new ClickEventButton(46,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, account);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }
}
