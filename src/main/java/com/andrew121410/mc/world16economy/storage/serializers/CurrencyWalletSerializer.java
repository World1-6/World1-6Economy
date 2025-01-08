package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16utils.config.serializers.SerializerUtils;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

public class CurrencyWalletSerializer implements TypeSerializer<CurrencyWallet> {
    @Override
    public CurrencyWallet deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) {
            return null;
        }

        UUID currencyUUID = SerializerUtils.nonVirtualNode(node, "currencyUUID").get(UUID.class);
        double amount = SerializerUtils.nonVirtualNode(node, "amount").get(Double.class);

        return new CurrencyWallet(currencyUUID, amount);
    }

    @Override
    public void serialize(Type type, @Nullable CurrencyWallet obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.node("currencyUUID").set(obj.getCurrencyUUID());
        node.node("amount").set(obj.getBalanceExact());
    }
}
