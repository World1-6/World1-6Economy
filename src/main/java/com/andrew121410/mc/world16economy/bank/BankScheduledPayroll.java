package com.andrew121410.mc.world16economy.bank;

import java.util.UUID;

public class BankScheduledPayroll {

    private final UUID accountUUID;
    private UUID currencyUUID;
    private long intervalMs;
    private long lastRunMs;
    private boolean onlineOnly; // if true, skip offline employees

    public BankScheduledPayroll(UUID accountUUID, UUID currencyUUID, long intervalMs, long lastRunMs) {
        this.accountUUID = accountUUID;
        this.currencyUUID = currencyUUID;
        this.intervalMs = intervalMs;
        this.lastRunMs = lastRunMs;
        this.onlineOnly = false;
    }

    public BankScheduledPayroll(UUID accountUUID, UUID currencyUUID, long intervalMs, long lastRunMs, boolean onlineOnly) {
        this.accountUUID = accountUUID;
        this.currencyUUID = currencyUUID;
        this.intervalMs = intervalMs;
        this.lastRunMs = lastRunMs;
        this.onlineOnly = onlineOnly;
    }

    public boolean isDue() {
        return System.currentTimeMillis() - lastRunMs >= intervalMs;
    }

    public UUID getAccountUUID() { return accountUUID; }

    public UUID getCurrencyUUID() { return currencyUUID; }
    public void setCurrencyUUID(UUID currencyUUID) { this.currencyUUID = currencyUUID; }

    public long getIntervalMs() { return intervalMs; }
    public void setIntervalMs(long intervalMs) { this.intervalMs = intervalMs; }

    public long getLastRunMs() { return lastRunMs; }
    public void setLastRunMs(long lastRunMs) { this.lastRunMs = lastRunMs; }

    public boolean isOnlineOnly() { return onlineOnly; }
    public void setOnlineOnly(boolean onlineOnly) { this.onlineOnly = onlineOnly; }
}
