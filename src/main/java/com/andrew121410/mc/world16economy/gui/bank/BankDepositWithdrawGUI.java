package com.andrew121410.mc.world16economy.gui.bank;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankAccount;
import com.andrew121410.mc.world16economy.bank.BankTransaction;
import com.andrew121410.mc.world16economy.bank.BankTier;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.GUIWindow;
import com.andrew121410.mc.world16utils.gui.GUIMultipageListWindow;
import com.andrew121410.mc.world16utils.gui.animation.Animation;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.CloneableGUIButton;
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

public class BankDepositWithdrawGUI {

    private static final TextColor GOLD   = TextColor.color(0xFFD700);
    private static final TextColor ORANGE = TextColor.color(0xFF8C00);
    private static final TextColor GREEN  = TextColor.color(0x00C853);
    private static final TextColor LIME   = TextColor.color(0x69FF47);
    private static final TextColor RED    = TextColor.color(0xFF1744);
    private static final TextColor RED2   = TextColor.color(0xFF6D00);
    private static final TextColor AQUA   = TextColor.color(0x00E5FF);
    private static final TextColor BLUE   = TextColor.color(0x2979FF);
    private static final TextColor GRAY   = TextColor.color(0x9E9E9E);

    // ── Step 1: Currency picker ──────────────────────────────────────────────

    public static void openCurrencyPicker(World16Economy plugin, Player player, BankAccount account, boolean isDeposit) {
        List<CloneableGUIButton> buttons = new ArrayList<>();

        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());

        for (Currency currency : plugin.getCurrenciesManager().getCurrenciesByUUID().values()) {
            double walletBal = wallet != null
                    ? wallet.getCurrencyWallets().getOrDefault(currency.getUuid(), new CurrencyWallet(currency.getUuid(), 0)).getBalanceExact()
                    : 0;
            double accountBal = account.getBalance(currency.getUuid());

            if (!isDeposit && accountBal <= 0) continue; // Nothing to withdraw

            buttons.add(((ClickEventButton) new ClickEventButton(0,
                    InventoryUtils.createItem(currency.getItemMaterial(), 1,
                            Translate.miniMessage("<bold>" + currency.getColor() + currency.getName())
                                    .decoration(TextDecoration.ITALIC, false),
                            isDeposit
                                    ? Translate.miniMessage("<dark_gray>▸ <gray>Wallet: " + currency.getColor() + String.format("%,.2f", walletBal))
                                    : Translate.miniMessage("<dark_gray>▸ <gray>Account: " + currency.getColor() + String.format("%,.2f", accountBal)),
                            Translate.miniMessage("<gray>Click to select")),
                    event -> openAmountPicker(plugin, player, account, currency, 0, isDeposit))
            ).animate(() -> InventoryUtils.createItem(currency.getItemMaterial(), 1,
                    Animation.wave(currency.getName(), GOLD, ORANGE)
                            .decoration(TextDecoration.ITALIC, false),
                    isDeposit
                            ? Translate.miniMessage("<dark_gray>▸ <gray>Wallet: " + currency.getColor() + String.format("%,.2f", walletBal))
                            : Translate.miniMessage("<dark_gray>▸ <gray>Account: " + currency.getColor() + String.format("%,.2f", accountBal)))));
        }

        if (buttons.isEmpty()) {
            buttons.add(new NoEventButton(0, InventoryUtils.createItem(Material.BARRIER, 1,
                    Translate.miniMessage("<red>No currencies available to withdraw."))));
        }

        GUIMultipageListWindow gui = new GUIMultipageListWindow(
                Translate.miniMessage(isDeposit
                        ? "<dark_gray>✦ Deposit: <green>" + account.getName()
                        : "<dark_gray>✦ Withdraw: <red>" + account.getName()), buttons);

        gui.getCustomBottomButtons().add(new ClickEventButton(46,
                InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                event -> {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    BankAccountGUI.open(plugin, player, account);
                }));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }

    // ── Step 2: Amount picker ────────────────────────────────────────────────

    public static void openAmountPicker(World16Economy plugin, Player player, BankAccount account,
                                        Currency currency, double currentAmount, boolean isDeposit) {
        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        double walletBal = wallet != null
                ? wallet.getCurrencyWallets().getOrDefault(currency.getUuid(), new CurrencyWallet(currency.getUuid(), 0)).getBalanceExact()
                : 0;
        double accountBal = account.getBalance(currency.getUuid());
        double maxAmount = isDeposit ? walletBal : accountBal;

        GUIWindow gui = new GUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                // Slot 4: currency + balance info
                buttons.add(new NoEventButton(4, InventoryUtils.createItem(currency.getItemMaterial(), 1,
                        Translate.miniMessage("<bold>" + currency.getColor() + currency.getName())
                                .decoration(TextDecoration.ITALIC, false),
                        isDeposit
                                ? Translate.miniMessage("<dark_gray>▸ <gray>Wallet: " + currency.getColor() + String.format("%,.2f", walletBal))
                                : Translate.miniMessage("<dark_gray>▸ <gray>Account: " + currency.getColor() + String.format("%,.2f", accountBal)))));

                // Slot 13: current amount display
                buttons.add(((NoEventButton) new NoEventButton(13, buildAmountDisplay(currency, currentAmount, isDeposit)))
                        .animate(() -> InventoryUtils.createItem(Material.PAPER, 1,
                                Animation.wave(String.format("%,.2f", currentAmount) + " " + currency.getCurrencyNamePlural(), GOLD, ORANGE)
                                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false),
                                Translate.miniMessage(isDeposit ? "<green>Amount to deposit" : "<red>Amount to withdraw"))));

                // Decrement buttons (row 2, left)
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 10, -10000, "<red>-10,000");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 11, -1000, "<red>-1,000");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 12, -100, "<red>-100");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 14, 100, "<green>+100");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 15, 1000, "<green>+1,000");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 16, 10000, "<green>+10,000");

                // Row 3
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 19, -10, "<red>-10");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 20, -5, "<red>-5");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 21, -1, "<red>-1");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 23, 1, "<green>+1");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 24, 5, "<green>+5");
                addIncrementButton(buttons, plugin, player, account, currency, currentAmount, isDeposit, maxAmount, 25, 10, "<green>+10");

                // Max button
                buttons.add(new ClickEventButton(22,
                        InventoryUtils.createItem(Material.NETHER_STAR, 1,
                                Translate.miniMessage("<yellow><bold>Max"),
                                Translate.miniMessage("<gray>Set to maximum available")),
                        event -> openAmountPicker(plugin, player, account, currency, maxAmount, isDeposit)));

                // Reset button
                buttons.add(new ClickEventButton(31,
                        InventoryUtils.createItem(Material.BARRIER, 1,
                                Translate.miniMessage("<gray>Reset"),
                                Translate.miniMessage("<gray>Set amount back to 0")),
                        event -> openAmountPicker(plugin, player, account, currency, 0, isDeposit)));

                // Type exact amount
                buttons.add(((ChatResponseButton) new ChatResponseButton(33,
                        InventoryUtils.createItem(Material.FEATHER, 1,
                                Translate.miniMessage("<white><bold>Type Exact Amount"),
                                Translate.miniMessage("<gray>Enter a precise number in chat")),
                        (Component) null, (Component) null,
                        (p, input) -> {
                            try {
                                double typed = Double.parseDouble(input.trim());
                                if (typed < 0) throw new NumberFormatException();
                                double clamped = Math.min(typed, maxAmount);
                                openAmountPicker(plugin, p, account, currency, clamped, isDeposit);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Translate.miniMessage("<red>✖ Invalid number."));
                                openAmountPicker(plugin, p, account, currency, currentAmount, isDeposit);
                            }
                        })
                ).animate(() -> InventoryUtils.createItem(Material.FEATHER, 1,
                        Animation.fading("Type Exact Amount", AQUA, BLUE),
                        Translate.miniMessage("<gray>Enter a precise number in chat"))));

                // Confirm button
                if (currentAmount > 0) {
                    buttons.add(((ClickEventButton) new ClickEventButton(40,
                            InventoryUtils.createItem(Material.EMERALD, 1,
                                    Translate.miniMessage(isDeposit ? "<green><bold>Confirm Deposit" : "<green><bold>Confirm Withdraw"),
                                    Translate.miniMessage("<dark_gray>▸ <gray>Amount: " + currency.getColor() + String.format("%,.2f", currentAmount) + " " + currency.getCurrencyNamePlural())),
                            event -> {
                                if (isDeposit) executeDeposit(plugin, player, account, currency, currentAmount);
                                else executeWithdraw(plugin, player, account, currency, currentAmount);
                            })
                    ).animate(() -> InventoryUtils.createItem(Material.EMERALD, 1,
                            Animation.wave(isDeposit ? "✔ Confirm Deposit" : "✔ Confirm Withdraw", GREEN, LIME),
                            Translate.miniMessage("<dark_gray>▸ " + currency.getColor() + String.format("%,.2f", currentAmount) + " " + currency.getCurrencyNamePlural()))));
                } else {
                    buttons.add(new NoEventButton(40, InventoryUtils.createItem(Material.GRAY_STAINED_GLASS_PANE, 1,
                            Translate.miniMessage("<gray>Set an amount first"))));
                }

                // Back
                buttons.add(new ClickEventButton(36,
                        InventoryUtils.createItem(Material.ARROW, 1, Translate.miniMessage("<gray>← Back")),
                        event -> {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                            openCurrencyPicker(plugin, player, account, isDeposit);
                        }));

                this.update(buttons,
                        Translate.miniMessage(isDeposit
                                ? "<dark_gray>✦ Deposit: <green>" + currency.getName()
                                : "<dark_gray>✦ Withdraw: <red>" + currency.getName()), 45);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {}
        };

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
        gui.open(player);
    }

    private static void addIncrementButton(List<AbstractGUIButton> buttons, World16Economy plugin, Player player,
                                           BankAccount account, Currency currency, double currentAmount,
                                           boolean isDeposit, double maxAmount, int slot, double delta, String label) {
        double newAmount = Math.max(0, Math.min(currentAmount + delta, maxAmount));
        boolean disabled = (delta > 0 && currentAmount >= maxAmount) || (delta < 0 && currentAmount <= 0);
        Material mat = delta > 0 ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        if (disabled) mat = Material.GRAY_STAINED_GLASS_PANE;

        final Material finalMat = mat;
        final double finalNew = newAmount;

        buttons.add(((ClickEventButton) new ClickEventButton(slot,
                InventoryUtils.createItem(finalMat, 1,
                        Translate.miniMessage("<bold>" + label).decoration(TextDecoration.ITALIC, false)),
                event -> {
                    if (disabled) return;
                    openAmountPicker(plugin, player, account, currency, finalNew, isDeposit);
                })
        ).animate(() -> InventoryUtils.createItem(finalMat, 1,
                Translate.miniMessage("<bold>" + label).decoration(TextDecoration.ITALIC, false))));
    }

    private static org.bukkit.inventory.ItemStack buildAmountDisplay(Currency currency, double amount, boolean isDeposit) {
        return InventoryUtils.createItem(Material.PAPER, 1,
                Translate.miniMessage("<bold>" + String.format("%,.2f", amount) + " " + currency.getCurrencyNamePlural())
                        .decoration(TextDecoration.ITALIC, false),
                Translate.miniMessage(isDeposit ? "<green>Amount to deposit" : "<red>Amount to withdraw"));
    }

    // ── Execute ──────────────────────────────────────────────────────────────

    private static void executeDeposit(World16Economy plugin, Player player, BankAccount account, Currency currency, double amount) {
        BankTier tier = plugin.getBankManager().getTier(account.getTierLevel());
        if (tier != null && tier.hasTransactionLimit() && amount > tier.getTransactionLimit()) {
            player.sendMessage(Translate.miniMessage("<red>✖ Amount exceeds transaction limit of <white>" + String.format("%,.0f", tier.getTransactionLimit()) + "<red>."));
            openAmountPicker(plugin, player, account, currency, amount, true);
            return;
        }
        if (tier != null && tier.hasBalanceLimit() && account.getBalance(currency.getUuid()) + amount > tier.getBalanceLimit()) {
            player.sendMessage(Translate.miniMessage("<red>✖ This would exceed the balance limit of <white>" + String.format("%,.0f", tier.getBalanceLimit()) + "<red>."));
            openAmountPicker(plugin, player, account, currency, amount, true);
            return;
        }

        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        CurrencyWallet cw = wallet != null ? wallet.getCurrencyWallets().get(currency.getUuid()) : null;
        if (cw == null || !cw.hasRequiredAmount(amount)) {
            player.sendMessage(Translate.miniMessage("<red>✖ Insufficient funds in your wallet."));
            openAmountPicker(plugin, player, account, currency, amount, true);
            return;
        }

        cw.subtractAmount(amount);
        plugin.getStorageManager().saveWallet(wallet, false);
        account.addBalance(currency.getUuid(), amount);
        plugin.getBankManager().saveBalances(account);
        plugin.getBankManager().recordTransaction(account, BankTransaction.Type.DEPOSIT, currency.getUuid(), amount, player.getUniqueId());

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.sendMessage(Translate.miniMessage("<green>✔ Deposited " + currency.getColor() + String.format("%,.2f", amount)
                + " " + currency.getCurrencyNamePlural() + " <green>into <white>" + account.getName() + "<green>."));
        BankAccountGUI.open(plugin, player, account);
    }

    private static void executeWithdraw(World16Economy plugin, Player player, BankAccount account, Currency currency, double amount) {
        BankTier tier = plugin.getBankManager().getTier(account.getTierLevel());
        if (tier != null && tier.hasTransactionLimit() && amount > tier.getTransactionLimit()) {
            player.sendMessage(Translate.miniMessage("<red>✖ Amount exceeds transaction limit of <white>" + String.format("%,.0f", tier.getTransactionLimit()) + "<red>."));
            openAmountPicker(plugin, player, account, currency, amount, false);
            return;
        }
        if (account.getBalance(currency.getUuid()) < amount) {
            player.sendMessage(Translate.miniMessage("<red>✖ Insufficient funds in this account."));
            openAmountPicker(plugin, player, account, currency, amount, false);
            return;
        }

        account.subtractBalance(currency.getUuid(), amount);
        plugin.getBankManager().saveBalances(account);

        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        if (wallet != null) {
            wallet.getCurrencyWallets().computeIfAbsent(currency.getUuid(), uuid -> new CurrencyWallet(uuid, 0)).addAmount(amount);
            plugin.getStorageManager().saveWallet(wallet, false);
        }
        plugin.getBankManager().recordTransaction(account, BankTransaction.Type.WITHDRAWAL, currency.getUuid(), amount, player.getUniqueId());

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.sendMessage(Translate.miniMessage("<green>✔ Withdrew " + currency.getColor() + String.format("%,.2f", amount)
                + " " + currency.getCurrencyNamePlural() + " <green>from <white>" + account.getName() + "<green>."));
        BankAccountGUI.open(plugin, player, account);
    }
}
