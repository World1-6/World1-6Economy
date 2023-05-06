package com.andrew121410.mc.world16economy.storage;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16economy.storage.serializers.CurrenciesManagerSerializer;
import com.andrew121410.mc.world16economy.storage.serializers.CurrencySerializer;
import com.andrew121410.mc.world16economy.storage.serializers.CurrencyWalletSerializer;
import com.andrew121410.mc.world16economy.storage.serializers.WalletSerializer;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.config.World16ConfigurateManager;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.TypeSerializerCollection;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class StorageManager {

    private World16Economy plugin;

    private YamlConfigurationLoader currenciesYml;
    private YamlConfigurationLoader walletsYml;

    public StorageManager(World16Economy plugin) {
        this.plugin = plugin;

        World16ConfigurateManager world16ConfigurateManager = new World16ConfigurateManager(this.plugin);
        world16ConfigurateManager.registerTypeSerializerCollection(getOurSerializers());

        this.currenciesYml = world16ConfigurateManager.getYamlConfigurationLoader("currencies.yml");
        this.walletsYml = world16ConfigurateManager.getYamlConfigurationLoader("wallets.yml");
    }

    private TypeSerializerCollection getOurSerializers() {
        TypeSerializerCollection.Builder typeSerializerCollection = TypeSerializerCollection.builder();

        typeSerializerCollection.registerExact(Currency.class, new CurrencySerializer());
        typeSerializerCollection.registerExact(CurrenciesManager.class, new CurrenciesManagerSerializer());
        typeSerializerCollection.registerExact(Wallet.class, new WalletSerializer());
        typeSerializerCollection.registerExact(CurrencyWallet.class, new CurrencyWalletSerializer());

        return typeSerializerCollection.build();
    }

    public void loadAllCurrencies() {
        // @TODO Load all currencies from the storage.
    }

    public void saveAllCurrencies() {
        // @TODO Save all currencies to the storage.
    }

    public void loadWallet(String userUuid) {
        // @TODO Load a wallet from the storage.
    }

    public void saveWallet(String userUuid, boolean removeFromCache) {
        // @TODO Save a wallet to the storage.
    }
}
