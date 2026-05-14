package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankMember;
import com.andrew121410.mc.world16economy.bank.BankRole;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankPayrollDestinationGUI {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);

    public static void open(World16Economy plugin, Player player, BankAccount sourceAccount) {
        BankMember member = sourceAccount.getMembers().get(player.getUniqueId());
        if (member == null) return;

        List<CloneableGUIButton> buttons = new ArrayList<>();

        // Personal wallet option
        boolean isPersonal = member.getPayrollDestinationAccountUUID() == null;
        buttons.add(((ClickEventButton) new ClickEventButton(0,
                InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                        Translate.miniMessage("<white><bold>Personal Wallet"),
                        Translate.miniMessage("<gray>Wages go directly to your wallet"),
                        isPersonal
                                ? Translate.miniMessage("<green>✔ Currently selected")
                                : Translate.miniMessage("<gray>Click to select")),
                event -> {
                    setDestination(plugin, player, sourceAccount, member, null);
                })
        ).animate(() -> InventoryUtils.createItem(Material.PLAYER_HEAD, 1,
                isPersonal
                        ? Animation.wave("✔ Personal Wallet", GREEN, LIME)
                        : Animation.fading("Personal Wallet", AQUA, BLUE),
                Translate.miniMessage("<gray>Wages go directly to your wallet"))));

        // All bank accounts the player owns or is a member of (excluding the source account)
        List<BankAccount> allAccounts = plugin.getBankManager().getAccountsForPlayer(player.getUniqueId());
        for (BankAccount dest : allAccounts) {
            if (dest.getAccountUUID().equals(sourceAccount.getAccountUUID())) continue;

            UUID destUUID = dest.getAccountUUID();
            boolean isSelected = destUUID.equals(member.getPayrollDestinationAccountUUID());
            BankRole destRole = dest.getRoleOf(player.getUniqueId());
            String roleStr = destRole != null ? destRole.name() : "Member";

            Material icon = dest.isBusiness() ? Material.CHEST : Material.BARREL;

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(icon, 1,
                            Translate.miniMessage("<white><bold>" + dest.getName()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Type: <white>" + (dest.isBusiness() ? "Business" : "Personal")),
                            Translate.miniMessage("<dark_gray>▸ <gray>Your role: <white>" + roleStr),
                            isSelected
                                    ? Translate.miniMessage("<green>✔ Currently selected")
                                    : Translate.miniMessage("<gray>Click to select")),
                    event -> {
                        setDestination(plugin, player, sourceAccount, member, destUUID);
                    })
            ).animate(() -> InventoryUtils.createItem(icon, 1,
                    isSelected
                            ? Animation.wave("✔ " + dest.getName(), GREEN, LIME)
                            : Animation.fading(dest.getName(), GOLD, ORANGE),
                    Translate.miniMessage("<dark_gray>▸ <gray>Type: <white>" + (dest.isBusiness() ? "Business" : "Personal")),
                    Translate.miniMessage("<dark_gray>▸ <gray>Your role: <white>" + roleStr))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Payroll Destination: <gold>" + sourceAccount.getName()), buttons);

        gui.getCustomBottomButtons().add(new ClickEventButton(46,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, sourceAccount);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }

    private static void setDestination(World16Economy plugin, Player player, BankAccount sourceAccount,
                                       BankMember member, UUID destinationUUID) {
        member.setPayrollDestinationAccountUUID(destinationUUID);
        plugin.getBankManager().saveMember(sourceAccount, member);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        if (destinationUUID == null) {
            player.sendMessage(Translate.miniMessage("<green>✔ Payroll will now go to your <white>personal wallet<green>."));
        } else {
            BankAccount dest = plugin.getBankManager().getAccount(destinationUUID);
            String name = dest != null ? dest.getName() : "selected account";
            player.sendMessage(Translate.miniMessage("<green>✔ Payroll will now go to <white>" + name + "<green>."));
        }
        BankPayrollDestinationGUI.open(plugin, player, sourceAccount);
    }
}
