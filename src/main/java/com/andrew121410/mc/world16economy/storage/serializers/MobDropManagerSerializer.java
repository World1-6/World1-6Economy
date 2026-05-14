package com.andrew121410.mc.world16economy.storage.serializers;

import com.andrew121410.mc.world16economy.currency.MobDropEntry;
import com.andrew121410.mc.world16economy.currency.MobDropManager;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.ConfigurationNode;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.SerializationException;
import com.andrew121410.mc.world16utils.dependencies.spongepowered.configurate.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MobDropManagerSerializer implements TypeSerializer<MobDropManager> {

    @Override
    public MobDropManager deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) return new MobDropManager();

        List<MobDropEntry> entries = new ArrayList<>();
        ConfigurationNode entriesNode = node.node("entries");
        if (!entriesNode.virtual() && entriesNode.isList()) {
            for (ConfigurationNode child : entriesNode.childrenList()) {
                MobDropEntry entry = child.get(MobDropEntry.class);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }

        return new MobDropManager(entries);
    }

    @Override
    public void serialize(Type type, @Nullable MobDropManager obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        ConfigurationNode entriesNode = node.node("entries");
        entriesNode.setList(MobDropEntry.class, obj.getMobDropEntries());
    }
}
