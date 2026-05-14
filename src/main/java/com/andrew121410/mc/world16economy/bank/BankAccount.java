package com.andrew121410.mc.world16economy.bank;

import java.util.*;

public class BankAccount {

    private final UUID accountUUID;
    private String name;
    private final UUID ownerUUID;
    private final BankAccountType type;
    private int tierLevel;

    // currencyUUID → balance
    private final Map<UUID, Double> balances = new LinkedHashMap<>();

    // playerUUID → member (only meaningful for BUSINESS accounts)
    private final Map<UUID, BankMember> members = new LinkedHashMap<>();

    // null if no scheduled payroll configured
    private BankScheduledPayroll scheduledPayroll;

    public BankAccount(UUID accountUUID, String name, UUID ownerUUID, BankAccountType type, int tierLevel) {
        this.accountUUID = accountUUID;
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.type = type;
        this.tierLevel = tierLevel;

        // Owner is always a member with OWNER role
        this.members.put(ownerUUID, new BankMember(ownerUUID, BankRole.OWNER, 0, null));
    }

    public double getBalance(UUID currencyUUID) {
        return balances.getOrDefault(currencyUUID, 0.0);
    }

    public void setBalance(UUID currencyUUID, double amount) {
        balances.put(currencyUUID, amount);
    }

    public void addBalance(UUID currencyUUID, double amount) {
        balances.merge(currencyUUID, amount, Double::sum);
    }

    public void subtractBalance(UUID currencyUUID, double amount) {
        balances.merge(currencyUUID, -amount, Double::sum);
    }

    public boolean hasMember(UUID playerUUID) {
        return members.containsKey(playerUUID);
    }

    public BankRole getRoleOf(UUID playerUUID) {
        BankMember member = members.get(playerUUID);
        return member != null ? member.getRole() : null;
    }

    public boolean isPersonal() {
        return type == BankAccountType.PERSONAL;
    }

    public boolean isBusiness() {
        return type == BankAccountType.BUSINESS;
    }

    public UUID getAccountUUID() { return accountUUID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getOwnerUUID() { return ownerUUID; }

    public BankAccountType getType() { return type; }

    public int getTierLevel() { return tierLevel; }
    public void setTierLevel(int tierLevel) { this.tierLevel = tierLevel; }

    public Map<UUID, Double> getBalances() { return balances; }

    public Map<UUID, BankMember> getMembers() { return members; }

    public BankScheduledPayroll getScheduledPayroll() { return scheduledPayroll; }
    public void setScheduledPayroll(BankScheduledPayroll scheduledPayroll) { this.scheduledPayroll = scheduledPayroll; }
}
