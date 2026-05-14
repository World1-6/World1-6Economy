package com.andrew121410.mc.world16economy.storage;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16utils.chat.Translate;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.SQLite;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.EasySQL;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.MultiTableEasySQL;
import com.andrew121410.mc.world16utils.dependencies.ccutils.storage.easy.SQLDataStore;
import com.andrew121410.mc.world16utils.utils.InventoryUtils;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteManager {

    public static final String NOTE_UUID_PDC_KEY = "note-uuid";

    private final World16Economy plugin;
    private final EasySQL easySQL;
    private final NamespacedKey namespacedKey;

    public NoteManager(World16Economy plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, NOTE_UUID_PDC_KEY);

        SQLite sqlite = new SQLite(plugin.getDataFolder(), "CurrencyNotes");
        MultiTableEasySQL multiTableEasySQL = new MultiTableEasySQL(sqlite);
        this.easySQL = new EasySQL("CurrencyNotes", multiTableEasySQL);

        List<String> columns = new ArrayList<>();
        columns.add("NoteUUID");
        columns.add("CurrencyUUID");
        columns.add("Amount");
        columns.add("CreatedBy");
        columns.add("CreatedAt");
        this.easySQL.create(columns, false);
    }

    // Saves a new note record and returns the item to give the player.
    public ItemStack createNote(UUID createdBy, Currency currency, double amount) {
        UUID noteUUID = UUID.randomUUID();

        SQLDataStore store = new SQLDataStore();
        store.put("NoteUUID", noteUUID.toString());
        store.put("CurrencyUUID", currency.getUuid().toString());
        store.put("Amount", String.valueOf(amount));
        store.put("CreatedBy", createdBy.toString());
        store.put("CreatedAt", String.valueOf(System.currentTimeMillis()));
        try {
            easySQL.save(store);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return buildItem(noteUUID, currency, amount);
    }

    // Returns the DB record for a note UUID, or null if it doesn't exist (already redeemed or forged).
    public SQLDataStore getNote(UUID noteUUID) {
        SQLDataStore query = new SQLDataStore();
        query.put("NoteUUID", noteUUID.toString());
        try {
            Multimap<String, SQLDataStore> result = easySQL.get(query);
            if (result != null && !result.isEmpty()) {
                return result.values().iterator().next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Deletes the note record — called on successful redemption to prevent re-use.
    public void deleteNote(UUID noteUUID) {
        SQLDataStore query = new SQLDataStore();
        query.put("NoteUUID", noteUUID.toString());
        try {
            easySQL.delete(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countNotesByCurrency(UUID currencyUUID) {
        SQLDataStore query = new SQLDataStore();
        query.put("CurrencyUUID", currencyUUID.toString());
        try {
            Multimap<String, SQLDataStore> result = easySQL.get(query);
            if (result != null) return result.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Reads the note UUID from an item's PersistentDataContainer, or null if not a note.
    public UUID getNoteUUID(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(namespacedKey, PersistentDataType.STRING)) return null;
        try {
            return UUID.fromString(pdc.get(namespacedKey, PersistentDataType.STRING));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ItemStack buildItem(UUID noteUUID, Currency currency, double amount) {
        String amountFormatted = amount % 1 == 0 ? String.valueOf((long) amount) : String.valueOf(amount);
        String currencyName = amount == 1 ? currency.getCurrencyNameSingular() : currency.getCurrencyNamePlural();

        ItemStack item = InventoryUtils.createItem(
                currency.getItemMaterial(),
                1,
                Translate.miniMessage("<bold>" + currency.getColor() + amountFormatted + " " + currencyName + " Note")
                        .decoration(TextDecoration.ITALIC, false),
                Translate.miniMessage("<gray>Currency: <white>" + currency.getName()),
                Translate.miniMessage("<gray>Amount: " + currency.getColor() + amountFormatted + " " + currencyName),
                Translate.miniMessage("<gray>Note ID: <dark_gray>" + noteUUID.toString().substring(0, 8) + "..."),
                Translate.miniMessage("<green>Right-click to redeem")
        );

        // Store only the note UUID — all other data is read from the DB on redeem
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(namespacedKey, PersistentDataType.STRING, noteUUID.toString()));

        return item;
    }
}
