package com.andrew121410.mc.world16economy.listeners;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.storage.NoteManager;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.SQLDataStore;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OnNoteRedeemListener implements Listener {

    private final World16Economy plugin;

    public OnNoteRedeemListener(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Only fire once (ignore off-hand duplicate event)
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        NoteManager noteManager = plugin.getNoteManager();
        UUID noteUUID = noteManager.getNoteUUID(item);
        if (noteUUID == null) return;

        event.setCancelled(true);

        // Look up the note in the database — this is the authoritative source
        SQLDataStore noteData = noteManager.getNote(noteUUID);
        if (noteData == null) {
            player.sendMessage(Translate.miniMessage("<red>This note is invalid or has already been redeemed."));
            return;
        }

        UUID currencyUUID;
        double amount;
        try {
            currencyUUID = UUID.fromString(noteData.get("CurrencyUUID"));
            amount = Double.parseDouble(noteData.get("Amount"));
        } catch (Exception e) {
            player.sendMessage(Translate.miniMessage("<red>This note is corrupted. Contact an admin."));
            return;
        }

        Currency currency = plugin.getCurrenciesManager().getCurrencyByUUID(currencyUUID);
        if (currency == null) {
            player.sendMessage(Translate.miniMessage("<red>The currency for this note no longer exists."));
            return;
        }

        Wallet wallet = plugin.getWalletManager().getWallets().get(player.getUniqueId());
        if (wallet == null) {
            player.sendMessage(Translate.miniMessage("<red>Could not find your wallet. Try rejoining."));
            return;
        }

        // Delete the record first to prevent any race condition on redemption
        noteManager.deleteNote(noteUUID);

        // Credit the player using the server-side amount, not whatever the item displays
        CurrencyWallet currencyWallet = wallet.getCurrencyWallets()
                .computeIfAbsent(currencyUUID, uuid -> new CurrencyWallet(uuid, 0));
        currencyWallet.addAmount(amount);
        plugin.getStorageManager().saveWallet(wallet);

        // Remove the note item from the player's hand
        item.setAmount(item.getAmount() - 1);

        String amountFormatted = amount % 1 == 0 ? String.valueOf((long) amount) : String.valueOf(amount);
        String currencyName = amount == 1 ? currency.getCurrencyNameSingular() : currency.getCurrencyNamePlural();
        player.sendMessage(Translate.miniMessage(
                "<green>Redeemed " + currency.getColor() + amountFormatted + " " + currencyName + "<green> successfully!"));
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
    }
}
