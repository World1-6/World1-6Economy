package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.user.CurrencyWallet;
import com.andrew121410.mc.world16economy.user.Wallet;
import com.andrew121410.mc.world16utils.config.serializers.SerializerUtils;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.TypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class WalletSerializer implements TypeSerializer<Wallet> {
    @Override
    public Wallet deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) return null;

        UUID uuid = SerializerUtils.nonVirtualNode(node, "uuid").get(UUID.class);

        TypeToken<Map<UUID, CurrencyWallet>> mapTypeToken = new TypeToken<>() {
        };

        Map<UUID, CurrencyWallet> currencyWallets = SerializerUtils.nonVirtualNode(node, "currencyWallets").get(mapTypeToken);

        return new Wallet(uuid, currencyWallets);
    }

    @Override
    public void serialize(Type type, @Nullable Wallet obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.node("uuid").set(obj.getUuid());

        TypeToken<Map<UUID, CurrencyWallet>> mapTypeToken = new TypeToken<>() {
        };

        node.node("currencyWallets").set(mapTypeToken, obj.getCurrencyWallets());
    }
}
