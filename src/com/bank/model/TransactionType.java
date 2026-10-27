package com.bank.model;

/**
 * Enumeration of financial transaction types.
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER_OUT("Transfer Sent"),
    TRANSFER_IN("Transfer Received"),
    INTEREST_CREDIT("Interest Credit"),
    FEE("Bank Fee");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
