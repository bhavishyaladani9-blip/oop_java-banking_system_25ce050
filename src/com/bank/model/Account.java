package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidPinException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Base Class: Account
 * Demonstrates Abstraction, Encapsulation, and Polymorphism.
 * All specific bank account types inherit from this base class.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String accountNumber;
    private final Customer customer;
    private String pin; // 4-digit security PIN
    protected double balance;
    private final LocalDateTime creationDate;
    private final AccountType accountType;
    private boolean active;
    private final List<Transaction> transactions;

    public Account(String accountNumber, Customer customer, String pin, double initialDeposit, AccountType accountType) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        if (pin == null || pin.trim().length() != 4 || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be a 4-digit number.");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }

        this.accountNumber = accountNumber;
        this.customer = customer;
        this.pin = pin;
        this.balance = initialDeposit;
        this.creationDate = LocalDateTime.now();
        this.accountType = accountType;
        this.active = true;
        this.transactions = new ArrayList<>();

        if (initialDeposit > 0) {
            this.transactions.add(new Transaction(
                    TransactionType.DEPOSIT,
                    initialDeposit,
                    this.balance,
                    "Initial account opening deposit",
                    null
            ));
        }
    }

    // --- Abstract Methods (Polymorphism) ---
    /**
     * Withdraws the specified amount according to specific account policies.
     */
    public abstract void withdraw(double amount) throws InsufficientFundsException;

    /**
     * Calculates the accrued interest based on the specific account rules.
     */
    public abstract double calculateInterest();

    /**
     * Applies the calculated interest directly to the account balance.
     */
    public abstract void applyInterest();

    /**
     * Returns detailed account-specific characteristics.
     */
    public abstract String getAccountSummary();

    // --- Concrete Methods (Common behavior) ---
    /**
     * Deposits money into the account.
     */
    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly positive.");
        }
        this.balance += amount;
        Transaction txn = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                this.balance,
                "Cash/Cheque Deposit",
                null
        );
        this.transactions.add(txn);
    }

    /**
     * Authenticates customer using 4-digit PIN.
     */
    public boolean authenticate(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    /**
     * Changes account PIN after verifying current PIN.
     */
    public void changePin(String oldPin, String newPin) throws InvalidPinException {
        if (!authenticate(oldPin)) {
            throw new InvalidPinException("Current PIN is incorrect.");
        }
        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("New PIN must be exactly 4 digits.");
        }
        this.pin = newPin;
    }

    /**
     * Appends an internal transaction record (e.g. transfer).
     */
    public synchronized void addTransaction(Transaction transaction) {
        if (transaction != null) {
            this.transactions.add(transaction);
        }
    }

    // Getters and Status Accessors
    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        return creationDate.format(DATE_FORMATTER);
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public String toString() {
        return String.format("[%s] No: %s | Holder: %s | Balance: $%.2f",
                accountType.getDisplayName(), accountNumber, customer.getName(), balance);
    }
}
