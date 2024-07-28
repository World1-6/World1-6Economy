package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletManager {

    private final World16Economy plugin;

    private Map<UUID, Wallet> wallets;

    public WalletManager(World16Economy plugin) {
        this.plugin = plugin;

        this.wallets = new HashMap<>();
    }

    public Wallet newUser(UUID uuid, boolean saveNow) {
        Wallet wallet = new Wallet(uuid);

        // Add the default currency to the wallet
        Currency defaultCurrency = this.plugin.getCurrenciesManager().getDefaultCurrency();
        CurrencyWallet currencyWallet = new CurrencyWallet(defaultCurrency.getUuid(), defaultCurrency.getDefaultMoney());
        wallet.getCurrencyWallets().putIfAbsent(defaultCurrency.getUuid(), currencyWallet);

        this.wallets.put(uuid, wallet);

        if (saveNow) {
            this.plugin.getStorageManager().saveWallet(wallet, false);
        }

        return wallet;
    }

    public Map<UUID, Wallet> getWallets() {
        return wallets;
    }

    public boolean hasWallet(UUID uuid) {
        return this.wallets.containsKey(uuid);
    }
}
