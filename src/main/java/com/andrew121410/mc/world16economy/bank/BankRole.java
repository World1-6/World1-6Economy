package com.andrew121410.mc.world16economy.bank;

public enum BankRole {
    OWNER,
    MANAGER,
    EMPLOYEE,
    VIEWER;

    public boolean canDeposit() {
        return this == OWNER || this == MANAGER || this == EMPLOYEE;
    }

    public boolean canWithdraw() {
        return this == OWNER || this == MANAGER;
    }

    public boolean canViewHistory() {
        return this == OWNER || this == MANAGER || this == VIEWER;
    }

    public boolean canManageMembers() {
        return this == OWNER || this == MANAGER;
    }

    public boolean canManagePayroll() {
        return this == OWNER;
    }

    public boolean canUpgrade() {
        return this == OWNER;
    }

    public boolean canDelete() {
        return this == OWNER;
    }
}
