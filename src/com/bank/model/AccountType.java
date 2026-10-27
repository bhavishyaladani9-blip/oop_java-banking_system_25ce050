package com.bank.model;

/**
 * Enumeration of supported bank account types.
 */
public enum AccountType {
    SAVINGS("Savings Account"),
    CURRENT("Current Account (Checking)"),
    FIXED_DEPOSIT("Fixed Deposit Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
