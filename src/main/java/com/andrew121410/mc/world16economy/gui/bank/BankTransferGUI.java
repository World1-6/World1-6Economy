package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankTransaction;
import com.andrew121410.mc.world16economy.bank.BankTier;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ChatResponseButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankTransferGUI {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);

    // Step 1: pick destination account
    public static void open(World16Economy plugin, Player player, BankAccount source) {
        List<BankAccount> allAccounts = plugin.getBankManager().getAccountsForPlayer(player.getUniqueId());
        List<CloneableGUIButton> buttons = new ArrayList<>();

        for (BankAccount dest : allAccounts) {
            if (dest.getAccountUUID().equals(source.getAccountUUID())) continue;

            Material icon = dest.isBusiness() ? Material.CHEST : Material.BARREL;
            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(icon, 1,
                            Translate.miniMessage("<white><bold>" + dest.getName()),
                            Translate.miniMessage("<dark_gray>▸ <gray>Type: <white>" + (dest.isBusiness() ? "Business" : "Personal")),
                            Translate.miniMessage("<gray>Click to transfer here")),
                    event -> openAmountStep(plugin, player, source, dest))
            ).animate(() -> InventoryUtils.createItem(icon, 1,
                    Animation.fading(dest.getName(), GOLD, ORANGE),
                    Translate.miniMessage("<dark_gray>▸ <gray>Type: <white>" + (dest.isBusiness() ? "Business" : "Personal")),
                    Translate.miniMessage("<gray>Click to transfer here"))));
        }

        if (buttons.isEmpty()) {
            buttons.add(new com.andrew121410.mc.world16utils.gui.buttons.defaults.NoEventButton(0,
                    InventoryUtils.createItem(Material.BARRIER, 1,
                            Translate.miniMessage("<red>No other accounts to transfer to."))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage("<dark_gray>✦ Transfer From: <white>" + source.getName()), buttons);

        gui.getCustomBottomButtons().add(new ClickEventButton(46,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, source);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }

    // Step 2: pick amount and currency via chat
    private static void openAmountStep(World16Economy plugin, Player player, BankAccount source, BankAccount dest) {
        // Build a simple one-button GUI prompting for amount input
        var gui = new com.andrew121410.mc.world16utils.gui.MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton> buttons = new ArrayList<>();

                buttons.add(((ChatResponseButton) new ChatResponseButton(0,
                        InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                                Translate.miniMessage("<white><bold>Transfer Amount"),
                                Translate.miniMessage("<dark_gray>▸ <gray>To: <white>" + dest.getName()),
                                Translate.miniMessage("<gray>Type: <white>500 <gray>or <white>500 dollars")),
                        (Component) null, (Component) null,
                        (p, input) -> handleTransfer(plugin, p, source, dest, input))
                ).animate(() -> InventoryUtils.createItem(Material.GOLD_INGOT, 1,
                        Animation.wave("Transfer Amount", GOLD, ORANGE),
                        Translate.miniMessage("<dark_gray>▸ <gray>To: <white>" + dest.getName()),
                        Translate.miniMessage("<gray>Type: <white>500 <gray>or <white>500 dollars"))));

                buttons.add(new ClickEventButton(0,
                        InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            BankTransferGUI.open(plugin, player, source);
                        }));

                this.update(buttons, Translate.miniMessage("<dark_gray>✦ Transfer To: <white>" + dest.getName()), null);
            }

            @Override
            public void onClose(org.bukkit.event.inventory.InventoryCloseEvent event) {}
        };

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }

    private static void handleTransfer(World16Economy plugin, Player player, BankAccount source, BankAccount dest, String input) {
        String[] parts = input.trim().split(" ", 2);
        double amount;
        try {
            amount = Double.parseDouble(parts[0]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Translate.miniMessage("<red>✖ Invalid amount."));
            openAmountStep(plugin, player, source, dest);
            return;
        }

        Currency currency;
        if (parts.length >= 2) {
            String name = parts[1];
            currency = plugin.getCurrenciesManager().getCurrenciesByUUID().values().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name)
                            || c.getCurrencyNameSingular().equalsIgnoreCase(name)
                            || c.getCurrencyNamePlural().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
            if (currency == null) {
                player.sendMessage(Translate.miniMessage("<red>✖ Unknown currency: <white>" + name));
                openAmountStep(plugin, player, source, dest);
                return;
            }
        } else {
            currency = plugin.getCurrenciesManager().getCurrencyByUUID(plugin.getCurrenciesManager().getDefaultCurrencyUUID());
            if (currency == null) {
                player.sendMessage(Translate.miniMessage("<red>✖ No default currency set."));
                BankAccountGUI.open(plugin, player, source);
                return;
            }
        }

        // Check source transaction limit
        BankTier tier = plugin.getBankManager().getTier(source.getTierLevel());
        if (tier != null && tier.hasTransactionLimit() && amount > tier.getTransactionLimit()) {
            player.sendMessage(Translate.miniMessage("<red>✖ Amount exceeds your account's transaction limit of <white>"
                    + String.format("%,.0f", tier.getTransactionLimit()) + "<red>."));
            openAmountStep(plugin, player, source, dest);
            return;
        }

        // Check source has enough
        if (source.getBalance(currency.getUuid()) < amount) {
            player.sendMessage(Translate.miniMessage("<red>✖ Insufficient funds in <white>" + source.getName() + "<red>."));
            openAmountStep(plugin, player, source, dest);
            return;
        }

        // Check dest balance limit
        BankTier destTier = plugin.getBankManager().getTier(dest.getTierLevel());
        if (destTier != null && destTier.hasBalanceLimit()
                && dest.getBalance(currency.getUuid()) + amount > destTier.getBalanceLimit()) {
            player.sendMessage(Translate.miniMessage("<red>✖ This would exceed <white>" + dest.getName()
                    + "<red>'s balance limit of <white>" + String.format("%,.0f", destTier.getBalanceLimit()) + "<red>."));
            openAmountStep(plugin, player, source, dest);
            return;
        }

        // Execute transfer
        source.subtractBalance(currency.getUuid(), amount);
        dest.addBalance(currency.getUuid(), amount);
        plugin.getBankManager().saveBalances(source);
        plugin.getBankManager().saveBalances(dest);
        plugin.getBankManager().recordTransaction(source, BankTransaction.Type.TRANSFER_OUT, currency.getUuid(), amount, player.getUniqueId());
        plugin.getBankManager().recordTransaction(dest, BankTransaction.Type.TRANSFER_IN, currency.getUuid(), amount, player.getUniqueId());

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.sendMessage(Translate.miniMessage("<green>✔ Transferred " + currency.getColor()
                + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural()
                + " <green>from <white>" + source.getName() + " <green>→ <white>" + dest.getName() + "<green>."));
        BankAccountGUI.open(plugin, player, source);
    }
}
