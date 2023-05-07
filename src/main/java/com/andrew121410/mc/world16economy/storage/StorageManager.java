package com.andrew121410.mc.world16economy.storage;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16economy.managers.WalletManager;
import com.andrew121410.mc.world16economy.storage.serializers.CurrencySerializer;
import com.andrew121410.mc.world16economy.storage.serializers.CurrencyWalletSerializer;
import com.andrew121410.mc.world16economy.storage.serializers.WalletSerializer;
import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.config.World16ConfigurateManager;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.CommentedConfigurationNode;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.TypeSerializerCollection;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.Map;
import java.util.UUID;

public class StorageManager {

    private World16Economy plugin;

    private YamlConfigurationLoader currenciesYml;
    private YamlConfigurationLoader walletsYml;

    private final CurrenciesManager currenciesManager;
    private final WalletManager walletManager;

    public StorageManager(World16Economy plugin) {
        this.plugin = plugin;

        this.currenciesManager = this.plugin.getCurrenciesManager();
        this.walletManager = this.plugin.getWalletManager();

        World16ConfigurateManager world16ConfigurateManager = new World16ConfigurateManager(this.plugin);
        world16ConfigurateManager.registerTypeSerializerCollection(getOurSerializers());

        this.currenciesYml = world16ConfigurateManager.getYamlConfigurationLoader("currencies.yml");
        this.walletsYml = world16ConfigurateManager.getYamlConfigurationLoader("wallets.yml");
    }

    private TypeSerializerCollection getOurSerializers() {
        TypeSerializerCollection.Builder typeSerializerCollection = TypeSerializerCollection.builder();

        typeSerializerCollection.registerExact(Currency.class, new CurrencySerializer());
        typeSerializerCollection.registerExact(Wallet.class, new WalletSerializer());
        typeSerializerCollection.registerExact(CurrencyWallet.class, new CurrencyWalletSerializer());

        return typeSerializerCollection.build();
    }

    public void loadAllCurrencies() {
        try {
            CommentedConfigurationNode node = this.currenciesYml.load().node("Currencies");

            for (Map.Entry<Object, CommentedConfigurationNode> objectCommentedConfigurationNodeEntry : node.childrenMap().entrySet()) {
                String key = (String) objectCommentedConfigurationNodeEntry.getKey();
                UUID uuid = UUID.fromString(key);
                CommentedConfigurationNode value = objectCommentedConfigurationNodeEntry.getValue();
                Currency currency = value.get(Currency.class);

                this.currenciesManager.getCurrenciesByUUID().putIfAbsent(uuid, currency);
            }

            // Setup first currency if there is none.
            if (this.currenciesManager.getCurrenciesByUUID().isEmpty()) {
                this.currenciesManager.setupFirstDefaultCurrency();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAllCurrencies() {
        try {
            CommentedConfigurationNode node = this.currenciesYml.load();

            for (Map.Entry<UUID, Currency> uuidCurrencyEntry : this.currenciesManager.getCurrenciesByUUID().entrySet()) {
                UUID uuid = uuidCurrencyEntry.getKey();
                Currency currency = uuidCurrencyEntry.getValue();

                node.node("Currencies", uuid.toString()).set(currency);
                this.currenciesYml.save(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveDefaultCurrencyUUID();
    }

    public Wallet loadWallet(String userUuid, boolean addToCache, boolean createIfNotExist) {
        try {
            CommentedConfigurationNode node = this.walletsYml.load().node("Wallets");

            // If the wallet is virtual, and we don't want to create it.
            if (node.node(userUuid).virtual() && !createIfNotExist) {
                return null;
            }

            // If the wallet is virtual, and we want to create it.
            if (node.node(userUuid).virtual() && createIfNotExist) {
                return this.walletManager.newUser(UUID.fromString(userUuid), true);
            }

            Wallet wallet = node.node(userUuid).get(Wallet.class);

            if (addToCache) {
                this.walletManager.getWallets().put(UUID.fromString(userUuid), wallet);
            }

            return wallet;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveWallet(Wallet wallet, boolean removeFromCache) {
        try {
            CommentedConfigurationNode node = this.walletsYml.load().node("Wallets");

            if (removeFromCache) {
                this.walletManager.getWallets().remove(wallet.getUuid());
            }

            node.node(wallet.getUuid().toString()).set(wallet);
            this.walletsYml.save(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID loadDefaultCurrencyUUID() {
        try {
            CommentedConfigurationNode node = this.currenciesYml.load().node("DefaultCurrency");

            if (node.virtual()) {
                return null;
            }

            return UUID.fromString(node.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveDefaultCurrencyUUID() {
        try {
            CommentedConfigurationNode node = this.currenciesYml.load();

            node.node("DefaultCurrency").set(this.currenciesManager.getDefaultCurrencyUUID().toString());
            this.currenciesYml.save(node);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
