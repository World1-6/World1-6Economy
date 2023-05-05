package com.andrew121410.mc.world16economy.user;

import java.util.Map;
import java.util.UUID;

public class Wallet {

    private UUID uuid; // Owner of the wallet.

    private Map<UUID, CurrencyWallet> currencyWallets;

    public Wallet(UUID owner) {
        this.uuid = owner;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean hasCurrency(UUID currencyUUID) {
        return this.currencyWallets.containsKey(currencyUUID);
    }

    public CurrencyWallet getCurrencyWallet(UUID currencyUUID) {
        return this.currencyWallets.get(currencyUUID);
    }

    public Map<UUID, CurrencyWallet> getCurrencyWallets() {
        return currencyWallets;
    }
}
