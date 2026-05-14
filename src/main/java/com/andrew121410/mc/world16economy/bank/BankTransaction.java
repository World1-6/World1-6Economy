package com.andrew121410.mc.world16economy.bank;

import java.util.UUID;

public class BankTransaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        PAYROLL_OUT,
        PAYROLL_IN,
        TRANSFER_OUT,
        TRANSFER_IN
    }

    private final UUID transactionUUID;
    private final UUID accountUUID;
    private final Type type;
    private final UUID currencyUUID;
    private final double amount;
    private final UUID actorUUID; // who triggered it
    private final long timestamp;

    public BankTransaction(UUID transactionUUID, UUID accountUUID, Type type, UUID currencyUUID, double amount, UUID actorUUID, long timestamp) {
        this.transactionUUID = transactionUUID;
        this.accountUUID = accountUUID;
        this.type = type;
        this.currencyUUID = currencyUUID;
        this.amount = amount;
        this.actorUUID = actorUUID;
        this.timestamp = timestamp;
    }

    public UUID getTransactionUUID() { return transactionUUID; }
    public UUID getAccountUUID() { return accountUUID; }
    public Type getType() { return type; }
    public UUID getCurrencyUUID() { return currencyUUID; }
    public double getAmount() { return amount; }
    public UUID getActorUUID() { return actorUUID; }
    public long getTimestamp() { return timestamp; }
}
