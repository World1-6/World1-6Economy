package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class bal implements CommandExecutor {

    private final World16Economy plugin;

    private WalletManager walletManager;

    public bal(World16Economy plugin) {
        this.plugin = plugin;

        this.walletManager = this.plugin.getWalletManager();

        this.plugin.getCommand("bal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only Players Can Use This Command.");
            return true;
        }

        if (!player.hasPermission("world16.bal")) {
            player.sendMessage(Translate.color("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            showAllCurrenciesGUI(player);
            return true;
        }

        return true;
    }

    private void showAllCurrenciesGUI(OfflinePlayer target, Player player) {
        Wallet wallet = this.walletManager.getWallets().get(target.getUniqueId());

        MiddleGUIWindow guiWindow = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                int slot = 0; // Doesn't matter
                for (Map.Entry<UUID, CurrencyWallet> uuidCurrencyWalletEntry : wallet.getCurrencyWallets().entrySet()) {
                    UUID uuid = uuidCurrencyWalletEntry.getKey();
                    CurrencyWallet currencyWallet = uuidCurrencyWalletEntry.getValue();

                    Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(uuid);

                    ItemStack itemStack = InventoryUtils.createItem(currency.getItemMaterial(), 1, currency.getName(), String.valueOf(currencyWallet.getAmount()));

                    ClickEventButton button = new ClickEventButton(slot, itemStack, (event) -> {
                    });

                    slot += 1; // Doesn't matter

                    buttons.add(button);
                }

                Component title = target.getUniqueId() == player.getUniqueId() ? Translate.miniMessage("<gold>Your wallet!") : Translate.miniMessage("<gold>" + target.getName() + "'s wallet!");

                this.update(buttons, title, null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {
            }
        };

        guiWindow.open(player);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
    }
}
