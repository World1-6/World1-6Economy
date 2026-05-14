package com.andrew121410.mc.world16economy.currency;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MobDropEntry {

    private long min;
    private long max;
    private double chance;
    // Empty list means this entry applies to all entity types
    private List<EntityType> entityTypes;
    // If true, drops a physical note item at the mob's location instead of crediting the wallet directly
    private boolean dropAsItem = false;

    public MobDropEntry(long min, long max, double chance) {
        this.entityTypes = new ArrayList<>();
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public MobDropEntry(long min, long max, double chance, List<EntityType> entityTypes, boolean dropAsItem) {
        this.min = min;
        this.max = max;
        this.chance = chance;
        this.entityTypes = entityTypes;
        this.dropAsItem = dropAsItem;
    }

    public boolean appliesTo(EntityType entityType) {
        return this.entityTypes.isEmpty() || this.entityTypes.contains(entityType);
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(List<EntityType> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public boolean isDropAsItem() {
        return dropAsItem;
    }

    public void setDropAsItem(boolean dropAsItem) {
        this.dropAsItem = dropAsItem;
    }
}
