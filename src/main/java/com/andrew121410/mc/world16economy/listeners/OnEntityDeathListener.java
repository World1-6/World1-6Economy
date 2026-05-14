package com.andrew121410.mc.world16economy.listeners;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.currency.MobDropEntry;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class OnEntityDeathListener implements Listener {

    private final World16Economy plugin;
    private final Random random = new Random();

    public OnEntityDeathListener(World16Economy plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        EntityType entityType = event.getEntityType();
        Wallet wallet = this.plugin.getWalletManager().getWallets().get(killer.getUniqueId());
        if (wallet == null) return;

        for (Map.Entry<UUID, Currency> entry : this.plugin.getCurrenciesManager().getCurrenciesByUUID().entrySet()) {
            Currency currency = entry.getValue();
            List<MobDropEntry> matching = currency.getMobDropManager().getMatchingEntries(entityType);

            for (MobDropEntry dropEntry : matching) {
                if (random.nextDouble() > dropEntry.getChance()) continue;

                long amount = dropEntry.getMin() + (long) (random.nextDouble() * (dropEntry.getMax() - dropEntry.getMin() + 1));
                if (amount <= 0) continue;

                String currencyName = amount == 1 ? currency.getCurrencyNameSingular() : currency.getCurrencyNamePlural();

                if (dropEntry.isDropAsItem()) {
                    // Spawn a physical note item at the mob's death location
                    ItemStack noteItem = this.plugin.getNoteManager().createNote(killer.getUniqueId(), currency, amount);
                    if (noteItem != null) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), noteItem);
                        killer.sendActionBar(MiniMessage.miniMessage().deserialize(
                                currency.getColor() + "+" + amount + " " + currencyName + " <gray>(note dropped)"
                        ));
                    }
                } else {
                    // Credit the wallet directly
                    CurrencyWallet currencyWallet = wallet.getCurrencyWallets().computeIfAbsent(
                            currency.getUuid(),
                            uuid -> new CurrencyWallet(uuid, 0)
                    );
                    currencyWallet.addAmount(amount);
                    plugin.getStorageManager().saveWallet(wallet);

                    killer.sendActionBar(MiniMessage.miniMessage().deserialize(
                            currency.getColor() + "+" + amount + " " + currencyName
                    ));
                }
            }
        }
    }
}
