package com.andrew121410.mc.world16economy.commands;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.gui.MiddleGUIWindow;
import com.andrew121410.mc.world16utils.gui.buttons.AbstractGUIButton;
import com.andrew121410.mc.world16utils.gui.buttons.defaults.ClickEventButton;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import org.bukkit.Material;
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

    private void showAllCurrenciesGUI(Player player) {
        Wallet wallet = this.walletManager.getWallets().get(player.getUniqueId());

        MiddleGUIWindow guiWindow = new MiddleGUIWindow() {
            @Override
            public void onCreate(Player player) {
                List<AbstractGUIButton> buttons = new ArrayList<>();

                int slot = 0;
                for (Map.Entry<UUID, CurrencyWallet> uuidCurrencyWalletEntry : wallet.getCurrencyWallets().entrySet()) {
                    UUID uuid = uuidCurrencyWalletEntry.getKey();
                    CurrencyWallet currencyWallet = uuidCurrencyWalletEntry.getValue();

                    ItemStack itemStack = InventoryUtils.createItem(Material.DIAMOND, 1, currencyWallet.getCurrencyUUID().toString(), String.valueOf(currencyWallet.getAmount()));

                    ClickEventButton button = new ClickEventButton(slot, itemStack, (event) -> {
                    });

                    slot += 1;

                    buttons.add(button);
                }

                this.update(buttons, "Testing", null);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {

            }
        };

        guiWindow.open(player);
    }
}
