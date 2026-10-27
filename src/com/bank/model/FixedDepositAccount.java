package com.bank.model;

import com.bank.exception.InsufficientFundsException;

import java.time.LocalDateTime;

/**
 * FixedDepositAccount represents a time-locked deposit account with higher yield.
 * Demonstrates specialized business logic, time calculations, and premature withdrawal penalties.
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_FD_RATE = 7.5; // 7.5% annual interest
    public static final double EARLY_WITHDRAWAL_PENALTY_RATE = 2.0; // 2% penalty on early closure

    private final int termMonths;
    private final double interestRate;
    private final LocalDateTime maturityDate;
    private boolean isMatured;

    public FixedDepositAccount(String accountNumber, Customer customer, String pin, double depositAmount, int termMonths) {
        this(accountNumber, customer, pin, depositAmount, termMonths, DEFAULT_FD_RATE);
    }

    public FixedDepositAccount(String accountNumber, Customer customer, String pin, double depositAmount, int termMonths, double interestRate) {
        super(accountNumber, customer, pin, depositAmount, AccountType.FIXED_DEPOSIT);

        if (termMonths < 1) {
            throw new IllegalArgumentException("Term must be at least 1 month.");
        }
        if (depositAmount <= 0) {
            throw new IllegalArgumentException("Fixed deposit amount must be greater than zero.");
        }

        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.maturityDate = getCreationDate().plusMonths(termMonths);
        this.isMatured = false;
    }

    @Override
    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        // In a fixed deposit, withdrawal is usually full liquidation
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }

        boolean premature = LocalDateTime.now().isBefore(maturityDate);
        double penalty = 0.0;

        if (premature) {
            penalty = (amount * EARLY_WITHDRAWAL_PENALTY_RATE) / 100.0;
        }

        double netPayout = amount - penalty;
        balance -= amount;

        String desc = premature
                ? String.format("Premature FD Liquidation (Penalty: $%.2f at %.1f%%)", penalty, EARLY_WITHDRAWAL_PENALTY_RATE)
                : "Matured FD Liquidation";

        addTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                balance,
                desc,
                null
        ));

        if (penalty > 0) {
            addTransaction(new Transaction(
                    TransactionType.FEE,
                    penalty,
                    balance,
                    "Early liquidation penalty",
                    null
            ));
        }
    }

    @Override
    public double calculateInterest() {
        // Compound interest: A = P * (1 + r/100)^years - P
        double years = termMonths / 12.0;
        return balance * (Math.pow((1 + (interestRate / 100.0)), years) - 1);
    }

    @Override
    public synchronized void applyInterest() {
        double expectedInterest = calculateInterest();
        if (expectedInterest > 0) {
            balance += expectedInterest;
            isMatured = true;
            addTransaction(new Transaction(
                    TransactionType.INTEREST_CREDIT,
                    expectedInterest,
                    balance,
                    String.format("FD Maturity Interest Credit (%.2f%% p.a. for %d months)", interestRate, termMonths),
                    null
            ));
        }
    }

    @Override
    public String getAccountSummary() {
        return String.format("Type: %s | Account: %s | Holder: %s | Principal: $%.2f | Rate: %.2f%% | Term: %d mos | Maturity: %s",
                getAccountType().getDisplayName(), getAccountNumber(), getCustomer().getName(),
                balance, interestRate, termMonths, maturityDate.format(DATE_FORMATTER));
    }

    public int getTermMonths() {
        return termMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public boolean isMatured() {
        return isMatured || LocalDateTime.now().isAfter(maturityDate);
    }
}
