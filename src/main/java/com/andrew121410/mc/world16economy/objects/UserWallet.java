package com.andrew121410.mc.world16economy.objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("UserWallet")
public class UserWallet implements ConfigurationSerializable {

    private UUID uuid;
    private long balance;

    public UserWallet(UUID uuid, long balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public static UserWallet deserialize(Map<String, Object> map) {
        return new UserWallet(UUID.fromString((String) map.get("UUID")), ((Number) map.get("Balance")).longValue());
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getBalanceExact() {
        return balance;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getBalanceFancy() {
        return "$" + balance;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void addBalance(long number) {
        this.balance += number;
    }

    public void subtractBalance(long number) {
        this.balance -= number;
    }

    public void multipleBalance(long number) {
        this.balance *= number;
    }

    public boolean hasEnough(long number) {
        return number <= this.balance;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("UUID", uuid.toString());
        map.put("Balance", this.balance);
        return map;
    }
}
