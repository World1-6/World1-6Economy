package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurrenciesManager {

    private Map<UUID, Currency> currenciesByUUID;
    private Map<String, Currency> currenciesByName;

    private UUID defaultCurrencyUUID;

    public CurrenciesManager(World16Economy plugin) {
        this.currenciesByName = new HashMap<>();
        this.currenciesByUUID = new HashMap<>();
    }


    // Used in serializer
    public CurrenciesManager(Map<UUID, Currency> currenciesByUUID) {
        this.currenciesByUUID = currenciesByUUID;

        // Populate the currenciesByName map
        this.currenciesByName = new HashMap<>();
        for (Currency currency : currenciesByUUID.values()) {
            this.currenciesByName.put(currency.getName(), currency);
        }
    }

    public void setupFirstDefaultCurrency() {
        if (this.currenciesByUUID.size() == 0) {
            Currency currency = new Currency("default", "$", "Dollar", "Dollars", 100);
            this.addCurrency(currency);
            this.setDefaultCurrencyUUID(currency.getUuid());

            World16Economy.getPlugin().getStorageManager().saveDefaultCurrencyUUID();
        }
    }

    public void addCurrency(Currency currency) {
        this.currenciesByName.put(currency.getName(), currency);
        this.currenciesByUUID.put(currency.getUuid(), currency);
    }

    public void removeCurrency(Currency currency) {
        this.currenciesByName.remove(currency.getName());
        this.currenciesByUUID.remove(currency.getUuid());
    }

    public Currency getDefaultCurrency() {
        return this.currenciesByUUID.get(this.defaultCurrencyUUID);
    }

    public Currency getCurrencyByName(String name) {
        return this.currenciesByName.get(name);
    }

    public Currency getCurrencyByUUID(UUID uuid) {
        return this.currenciesByUUID.get(uuid);
    }

    public boolean hasCurrencyByName(String name) {
        return this.currenciesByName.containsKey(name);
    }

    public boolean hasCurrencyByUUID(UUID uuid) {
        return this.currenciesByUUID.containsKey(uuid);
    }

    public UUID getDefaultCurrencyUUID() {
        return defaultCurrencyUUID;
    }

    public void setDefaultCurrencyUUID(UUID defaultCurrencyUUID) {
        this.defaultCurrencyUUID = defaultCurrencyUUID;
    }

    public Map<UUID, Currency> getCurrenciesByUUID() {
        return currenciesByUUID;
    }
}
