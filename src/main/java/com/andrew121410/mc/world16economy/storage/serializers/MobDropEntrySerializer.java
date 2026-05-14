package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.currency.MobDropEntry;
import com.andrew121410.mc.world16utils.config.serializers.SerializerUtils;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.TypeSerializer;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MobDropEntrySerializer implements TypeSerializer<MobDropEntry> {

    @Override
    public MobDropEntry deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) return null;

        long min = SerializerUtils.nonVirtualNode(node, "min").getLong();
        long max = SerializerUtils.nonVirtualNode(node, "max").getLong();
        double chance = SerializerUtils.nonVirtualNode(node, "chance").getDouble();

        List<EntityType> entityTypes = new ArrayList<>();
        ConfigurationNode entityTypesNode = SerializerUtils.nonVirtualNode(node, "entityTypes");
        if (!entityTypesNode.virtual() && entityTypesNode.isList()) {
            for (ConfigurationNode child : entityTypesNode.childrenList()) {
                String name = child.getString();
                if (name != null) {
                    try {
                        entityTypes.add(EntityType.valueOf(name.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        boolean dropAsItem = !SerializerUtils.nonVirtualNode(node, "dropAsItem").virtual()
                && SerializerUtils.nonVirtualNode(node, "dropAsItem").getBoolean();

        return new MobDropEntry(min, max, chance, entityTypes, dropAsItem);
    }

    @Override
    public void serialize(Type type, @Nullable MobDropEntry obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.node("min").set(obj.getMin());
        node.node("max").set(obj.getMax());
        node.node("chance").set(obj.getChance());

        List<String> entityTypeNames = new ArrayList<>();
        for (EntityType entityType : obj.getEntityTypes()) {
            entityTypeNames.add(entityType.name());
        }
        node.node("entityTypes").setList(String.class, entityTypeNames);
        node.node("dropAsItem").set(obj.isDropAsItem());
    }
}
