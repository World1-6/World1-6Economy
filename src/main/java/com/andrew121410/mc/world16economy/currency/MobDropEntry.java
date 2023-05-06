package com.andrew121410.mc.world16economy.currency;

import org.bukkit.entity.Creature;

import java.util.ArrayList;
import java.util.List;

public class MobDropEntry {

    private long min;
    private long max;
    private double chance;
    private List<Creature> creatures;

    public MobDropEntry(long min, long max, double chance) {
        this.creatures = new ArrayList<>();

        this.min = min;
        this.max = max;
        this.chance = chance;
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

    public List<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<Creature> creatures) {
        this.creatures = creatures;
    }
}
