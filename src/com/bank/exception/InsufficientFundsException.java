package com.bank.exception;

/**
 * Thrown when a withdrawal or transfer cannot be completed due to insufficient funds/overdraft.
 */
public class InsufficientFundsException extends BankingException {
    private final double currentBalance;
    private final double requestedAmount;

    public InsufficientFundsException(double currentBalance, double requestedAmount) {
        super(String.format("Insufficient funds! Current balance: $%.2f, Requested amount: $%.2f", currentBalance, requestedAmount));
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public InsufficientFundsException(String message) {
        super(message);
        this.currentBalance = 0;
        this.requestedAmount = 0;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }
}
