package com.bank.model;

import com.bank.exception.InsufficientFundsException;

/**
 * CurrentAccount (Checking) tailored for business or high-volume transactions.
 * Supports an Overdraft facility allowing balance to drop below zero up to a set limit.
 * Demonstrates Polymorphism and business logic specialization.
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_OVERDRAFT_LIMIT = 1000.00;
    public static final double OVERDRAFT_FEE_RATE = 1.5; // 1.5% fee on overdraft usage

    private double overdraftLimit;
    private double transactionFee;

    public CurrentAccount(String accountNumber, Customer customer, String pin, double initialDeposit) {
        this(accountNumber, customer, pin, initialDeposit, DEFAULT_OVERDRAFT_LIMIT, 0.0);
    }

    public CurrentAccount(String accountNumber, Customer customer, String pin, double initialDeposit,
                          double overdraftLimit, double transactionFee) {
        super(accountNumber, customer, pin, initialDeposit, AccountType.CURRENT);
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
        this.transactionFee = transactionFee;
    }

    @Override
    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        double totalDeduction = amount + transactionFee;
        double maxAvailable = balance + overdraftLimit;

        if (totalDeduction > maxAvailable) {
            throw new InsufficientFundsException(String.format(
                    "Exceeds available funds and overdraft! Balance: $%.2f, Overdraft Limit: $%.2f, Requested: $%.2f (Fee: $%.2f)",
                    balance, overdraftLimit, amount, transactionFee));
        }

        balance -= totalDeduction;

        String desc = transactionFee > 0
                ? String.format("Current Account Withdrawal (incl. $%.2f fee)", transactionFee)
                : "Current Account Withdrawal";

        addTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                balance,
                desc,
                null
        ));

        if (transactionFee > 0) {
            addTransaction(new Transaction(
                    TransactionType.FEE,
                    transactionFee,
                    balance,
                    "Transaction processing fee",
                    null
            ));
        }
    }

    @Override
    public double calculateInterest() {
        // Standard Current accounts earn 0 interest; if in overdraft, small maintenance fee could be assessed.
        return 0.0;
    }

    @Override
    public synchronized void applyInterest() {
        // No interest credited to checking account
    }

    @Override
    public String getAccountSummary() {
        return String.format("Type: %s | Account: %s | Holder: %s | Balance: $%.2f | Overdraft Limit: $%.2f | Available: $%.2f",
                getAccountType().getDisplayName(), getAccountNumber(), getCustomer().getName(),
                balance, overdraftLimit, (balance + overdraftLimit));
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }
}
