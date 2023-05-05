package com.andrew121410.mc.world16economy.managers;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.user.Wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletManager {

    private Map<UUID, Wallet> wallets;

    public WalletManager(World16Economy plugin) {
        this.wallets = new HashMap<>();
    }

    public void loadAll() {
        //TODO
    }

    public void saveAll() {
        //TODO
    }

    public Map<UUID, Wallet> getWallets() {
        return wallets;
    }

    public boolean hasWallet(UUID uuid) {
        return this.wallets.containsKey(uuid);
    }
}
