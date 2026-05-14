package com.andrew121410.mc.world16economy.bank;

import java.util.UUID;

public class BankMember {

    private UUID playerUUID;
    private BankRole role;
    private double wage;
    // null = personal wallet, otherwise a bank account UUID
    private UUID payrollDestinationAccountUUID;

    public BankMember(UUID playerUUID, BankRole role, double wage, UUID payrollDestinationAccountUUID) {
        this.playerUUID = playerUUID;
        this.role = role;
        this.wage = wage;
        this.payrollDestinationAccountUUID = payrollDestinationAccountUUID;
    }

    public UUID getPlayerUUID() { return playerUUID; }

    public BankRole getRole() { return role; }
    public void setRole(BankRole role) { this.role = role; }

    public double getWage() { return wage; }
    public void setWage(double wage) { this.wage = wage; }

    public UUID getPayrollDestinationAccountUUID() { return payrollDestinationAccountUUID; }
    public void setPayrollDestinationAccountUUID(UUID payrollDestinationAccountUUID) { this.payrollDestinationAccountUUID = payrollDestinationAccountUUID; }
}
