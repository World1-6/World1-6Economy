package com.andrew121410.mc.world16economy.currency;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MobDropManager {

    private List<MobDropEntry> mobDropEntries;

    public MobDropManager() {
        this.mobDropEntries = new ArrayList<>();
    }

    public MobDropManager(List<MobDropEntry> mobDropEntries) {
        this.mobDropEntries = mobDropEntries;
    }

    public List<MobDropEntry> getMatchingEntries(EntityType entityType) {
        List<MobDropEntry> matching = new ArrayList<>();
        for (MobDropEntry entry : mobDropEntries) {
            if (entry.appliesTo(entityType)) {
                matching.add(entry);
            }
        }
        return matching;
    }

    public List<MobDropEntry> getMobDropEntries() {
        return mobDropEntries;
    }
}
