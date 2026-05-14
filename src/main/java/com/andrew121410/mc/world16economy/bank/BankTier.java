package com.andrew121410.mc.world16economy.bank;

public class BankTier {

    private int level;
    private String displayName;
    private double creationCost;
    private double balanceLimit;      // -1 = unlimited
    private double transactionLimit;  // -1 = unlimited

    public BankTier(int level, String displayName, double creationCost, double balanceLimit, double transactionLimit) {
        this.level = level;
        this.displayName = displayName;
        this.creationCost = creationCost;
        this.balanceLimit = balanceLimit;
        this.transactionLimit = transactionLimit;
    }

    public boolean hasBalanceLimit() {
        return balanceLimit >= 0;
    }

    public boolean hasTransactionLimit() {
        return transactionLimit >= 0;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public double getCreationCost() { return creationCost; }
    public void setCreationCost(double creationCost) { this.creationCost = creationCost; }

    public double getBalanceLimit() { return balanceLimit; }
    public void setBalanceLimit(double balanceLimit) { this.balanceLimit = balanceLimit; }

    public double getTransactionLimit() { return transactionLimit; }
    public void setTransactionLimit(double transactionLimit) { this.transactionLimit = transactionLimit; }
}
