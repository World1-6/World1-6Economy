package com.andrew121410.mc.world16economy.user;

import java.text.DecimalFormat;
import java.util.UUID;

public class CurrencyWallet {

    private UUID currencyUUID;
    private double balanceExact;

    public CurrencyWallet(UUID currencyUUID, double balanceExact) {
        this.currencyUUID = currencyUUID;
        this.balanceExact = balanceExact;
    }

    public String getBalanceDecimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###"); // #,###,###,##0.00
        return decimalFormat.format(this.balanceExact);
    }

    public boolean hasRequiredAmount(double amount) {
        return this.balanceExact >= amount;
    }

    public void addAmount(double amount) {
        this.balanceExact += amount;
    }

    public void subtractAmount(double amount) {
        this.balanceExact -= amount;
    }

    public UUID getCurrencyUUID() {
        return currencyUUID;
    }

    public double getBalanceExact() {
        return balanceExact;
    }

    public void setBalanceExact(double amount) {
        this.balanceExact = amount;
    }
}
