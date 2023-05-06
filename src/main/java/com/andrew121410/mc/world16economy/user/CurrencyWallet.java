package com.andrew121410.mc.world16economy.user;

import java.util.UUID;

public class CurrencyWallet {

    private UUID currencyUUID;
    private double amount;

    public CurrencyWallet(UUID currencyUUID, double amount) {
        this.currencyUUID = currencyUUID;
        this.amount = amount;
    }

    public boolean hasRequiredAmount(double amount) {
        return this.amount >= amount;
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }

    public void subtractAmount(double amount) {
        this.amount -= amount;
    }

    public UUID getCurrencyUUID() {
        return currencyUUID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
