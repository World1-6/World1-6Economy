package com.andrew121410.mc.world16economy.currency;

import java.util.UUID;

public class Currency {

    private String name;
    private UUID uuid; // The uuid of the currency. (doesn't change)

    private boolean defaultCurrency;

    // Managers
    private MobDropManager mobDropManager;

    private String symbol;
    private String currencyNameSingular;
    private String currencyNamePlural;

    private long defaultMoney;

    public Currency(String name, String symbol, String currencyNameSingular, String currencyNamePlural, long defaultMoney) {
        this.uuid = UUID.randomUUID();

        this.name = name;
        this.symbol = symbol;
        this.currencyNameSingular = currencyNameSingular;
        this.currencyNamePlural = currencyNamePlural;
        this.defaultMoney = defaultMoney;

        this.mobDropManager = new MobDropManager();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(boolean defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCurrencyNameSingular() {
        return currencyNameSingular;
    }

    public void setCurrencyNameSingular(String currencyNameSingular) {
        this.currencyNameSingular = currencyNameSingular;
    }

    public String getCurrencyNamePlural() {
        return currencyNamePlural;
    }

    public void setCurrencyNamePlural(String currencyNamePlural) {
        this.currencyNamePlural = currencyNamePlural;
    }

    public long getDefaultMoney() {
        return defaultMoney;
    }

    public void setDefaultMoney(long defaultMoney) {
        this.defaultMoney = defaultMoney;
    }
}
