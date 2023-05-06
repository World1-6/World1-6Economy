package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.currency.Currency;
import com.andrew121410.mc.world16economy.currency.MobDropManager;
import com.andrew121410.mc.world16utils.config.serializers.SerializerUtils;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.utils.spongepowered.configurate.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

public class CurrencySerializer implements TypeSerializer<Currency> {
    @Override
    public Currency deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) return null;

        UUID uuid = SerializerUtils.nonVirtualNode(node, "uuid").get(UUID.class);
        String name = SerializerUtils.nonVirtualNode(node, "name").getString();
        Boolean defaultCurrency = SerializerUtils.nonVirtualNode(node, "defaultCurrency").getBoolean();
        String symbol = SerializerUtils.nonVirtualNode(node, "symbol").getString();
        String currencyNameSingular = SerializerUtils.nonVirtualNode(node, "currencyNameSingular").getString();
        String currencyNamePlural = SerializerUtils.nonVirtualNode(node, "currencyNamePlural").getString();
        Double defaultMoney = SerializerUtils.nonVirtualNode(node, "defaultMoney").getDouble();

        MobDropManager mobDropManager = SerializerUtils.nonVirtualNode(node, "mobDropManager").get(MobDropManager.class);

        return new Currency(
                name,
                uuid,
                defaultCurrency,
                mobDropManager,
                symbol,
                currencyNameSingular,
                currencyNamePlural,
                defaultMoney
        );
    }

    @Override
    public void serialize(Type type, @Nullable Currency obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.node("uuid").set(obj.getUuid());
        node.node("name").set(obj.getName());
        node.node("defaultCurrency").set(obj.isDefaultCurrency());
        node.node("symbol").set(obj.getSymbol());
        node.node("currencyNameSingular").set(obj.getCurrencyNameSingular());
        node.node("currencyNamePlural").set(obj.getCurrencyNamePlural());
        node.node("defaultMoney").set(obj.getDefaultMoney());
        node.node("mobDropManager").set(obj.getMobDropManager());
    }
}
