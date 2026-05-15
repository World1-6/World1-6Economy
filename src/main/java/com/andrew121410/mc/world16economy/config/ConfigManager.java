package com.andrew121410.mc.world16economy.config;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.bank.BankTier;
import com.andrew121410.mc.world16utils.config.CustomYmlManager;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final CustomYmlManager yml;

    public ConfigManager(World16Economy plugin) {
        this.yml = new CustomYmlManager(plugin);
        this.yml.setup("config.yml");

        if (this.yml.isNew()) {
            writeDefaults();
        }
    }

    private void writeDefaults() {
        yml.getConfig().set("bank.tiers.1.display-name", "Basic");
        yml.getConfig().set("bank.tiers.1.creation-cost", 500.0);
        yml.getConfig().set("bank.tiers.1.balance-limit", 10000.0);
        yml.getConfig().set("bank.tiers.1.transaction-limit", 1000.0);

        yml.getConfig().set("bank.tiers.2.display-name", "Standard");
        yml.getConfig().set("bank.tiers.2.creation-cost", 2000.0);
        yml.getConfig().set("bank.tiers.2.balance-limit", 100000.0);
        yml.getConfig().set("bank.tiers.2.transaction-limit", 10000.0);

        yml.getConfig().set("bank.tiers.3.display-name", "Premium");
        yml.getConfig().set("bank.tiers.3.creation-cost", 10000.0);
        yml.getConfig().set("bank.tiers.3.balance-limit", -1.0);
        yml.getConfig().set("bank.tiers.3.transaction-limit", -1.0);

        yml.saveConfig();
    }

    public List<BankTier> loadBankTiers() {
        List<BankTier> tiers = new ArrayList<>();

        if (!yml.getConfig().isConfigurationSection("bank.tiers")) {
            return tiers;
        }

        for (String key : yml.getConfig().getConfigurationSection("bank.tiers").getKeys(false)) {
            try {
                int level = Integer.parseInt(key);
                String path = "bank.tiers." + key;
                String displayName = yml.getConfig().getString(path + ".display-name", "Tier " + level);
                double creationCost = yml.getConfig().getDouble(path + ".creation-cost", 0);
                double balanceLimit = yml.getConfig().getDouble(path + ".balance-limit", -1);
                double transactionLimit = yml.getConfig().getDouble(path + ".transaction-limit", -1);
                tiers.add(new BankTier(level, displayName, creationCost, balanceLimit, transactionLimit));
            } catch (NumberFormatException e) {
                // Skip malformed tier keys
            }
        }

        return tiers;
    }
}
