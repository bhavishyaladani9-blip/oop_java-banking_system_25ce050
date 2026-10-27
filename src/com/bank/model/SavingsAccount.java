package com.bank.model;

import com.bank.exception.InsufficientFundsException;

/**
 * SavingsAccount represents an interest-bearing personal deposit account.
 * Demonstrates Inheritance and Polymorphic method overriding.
 * Enforces minimum balance requirements and daily withdrawal limits.
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_INTEREST_RATE = 4.25; // 4.25% annual
    public static final double DEFAULT_MIN_BALANCE = 100.00;
    public static final double DEFAULT_DAILY_LIMIT = 2500.00;

    private double interestRate;
    private double minimumBalance;
    private double dailyWithdrawalLimit;
    private double dailyWithdrawnAmount;

    public SavingsAccount(String accountNumber, Customer customer, String pin, double initialDeposit) {
        this(accountNumber, customer, pin, initialDeposit, DEFAULT_INTEREST_RATE, DEFAULT_MIN_BALANCE, DEFAULT_DAILY_LIMIT);
    }

    public SavingsAccount(String accountNumber, Customer customer, String pin, double initialDeposit,
                          double interestRate, double minimumBalance, double dailyWithdrawalLimit) {
        super(accountNumber, customer, pin, initialDeposit, AccountType.SAVINGS);

        if (initialDeposit < minimumBalance) {
            throw new IllegalArgumentException(String.format("Initial deposit ($%.2f) must be at least minimum balance ($%.2f)",
                    initialDeposit, minimumBalance));
        }
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.dailyWithdrawnAmount = 0.0;
    }

    @Override
    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (dailyWithdrawnAmount + amount > dailyWithdrawalLimit) {
            throw new InsufficientFundsException(String.format(
                    "Daily withdrawal limit ($%.2f) exceeded! Today's withdrawals: $%.2f, Requested: $%.2f",
                    dailyWithdrawalLimit, dailyWithdrawnAmount, amount));
        }
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal violates minimum balance requirement ($%.2f). Current: $%.2f, Requested: $%.2f",
                    minimumBalance, balance, amount));
        }

        balance -= amount;
        dailyWithdrawnAmount += amount;

        addTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                balance,
                "ATM/Branch Cash Withdrawal",
                null
        ));
    }

    @Override
    public double calculateInterest() {
        return (balance * (interestRate / 100.0)) / 12.0; // Monthly equivalent
    }

    @Override
    public synchronized void applyInterest() {
        double monthlyInterest = calculateInterest();
        if (monthlyInterest > 0) {
            balance += monthlyInterest;
            addTransaction(new Transaction(
                    TransactionType.INTEREST_CREDIT,
                    monthlyInterest,
                    balance,
                    String.format("Monthly Interest Credit (%.2f%% p.a.)", interestRate),
                    null
            ));
        }
    }

    @Override
    public String getAccountSummary() {
        return String.format("Type: %s | Account: %s | Holder: %s | Balance: $%.2f | Int. Rate: %.2f%% | Min Bal: $%.2f | Daily Limit: $%.2f",
                getAccountType().getDisplayName(), getAccountNumber(), getCustomer().getName(),
                balance, interestRate, minimumBalance, dailyWithdrawalLimit);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void resetDailyWithdrawnAmount() {
        this.dailyWithdrawnAmount = 0.0;
    }
}
