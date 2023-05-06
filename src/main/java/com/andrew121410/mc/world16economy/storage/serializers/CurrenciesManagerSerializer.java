package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.managers.CurrenciesManager;
import com.andrew121410.mc.world16utils.config.serializers.SerializerUtils;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.TypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class CurrenciesManagerSerializer implements TypeSerializer<CurrenciesManager> {
    @Override
    public CurrenciesManager deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) return null;

        TypeToken<Map<UUID, Currency>> uuidCurrencyTypeToken = new TypeToken<>() {
        };
        Map<UUID, Currency> uuidCurrentMap = SerializerUtils.nonVirtualNode(node, "uuidCurrencyMap").get(uuidCurrencyTypeToken);

        return new CurrenciesManager(uuidCurrentMap);
    }

    @Override
    public void serialize(Type type, @Nullable CurrenciesManager currenciesManager, ConfigurationNode node) throws SerializationException {
        if (currenciesManager == null) {
            node.raw(null);
            return;
        }

        TypeToken<Map<UUID, Currency>> uuidCurrencyTypeToken = new TypeToken<>() {
        };

        node.node("uuidCurrencyMap").set(uuidCurrencyTypeToken, currenciesManager.getCurrenciesByUUID());
    }
}
