package com.bank.service;

import com.bank.exception.BankingException;
import com.bank.model.*;

import java.util.List;

/**
 * Service interface specifying all core and advanced banking business operations.
 */
public interface BankingService {
    // --- Account Management ---
    SavingsAccount createSavingsAccount(String customerName, String email, String phone, String address,
                                        String pin, double initialDeposit) throws BankingException;

    CurrentAccount createCurrentAccount(String customerName, String email, String phone, String address,
                                        String pin, double initialDeposit, double overdraftLimit) throws BankingException;

    FixedDepositAccount createFixedDepositAccount(String customerName, String email, String phone, String address,
                                                  String pin, double depositAmount, int termMonths) throws BankingException;

    Account login(String accountNumber, String pin) throws BankingException;

    void changePin(String accountNumber, String oldPin, String newPin) throws BankingException;

    Account getAccount(String accountNumber) throws BankingException;

    // --- Financial Operations ---
    void deposit(String accountNumber, double amount) throws BankingException;

    void withdraw(String accountNumber, String pin, double amount) throws BankingException;

    void transfer(String fromAccountNumber, String toAccountNumber, String pin, double amount) throws BankingException;

    double getBalance(String accountNumber, String pin) throws BankingException;

    List<Transaction> getTransactionHistory(String accountNumber, String pin) throws BankingException;

    // --- Admin Operations ---
    List<Account> getAllAccounts();

    double getTotalBankBalance();

    int applyInterestToAll();

    void seedSampleData();

    // --- Financial Tools (Extra Features) ---
    double calculateLoanEMI(double principal, double annualRatePercentage, int tenureMonths);

    double calculateFixedDepositMaturity(double principal, double annualRatePercentage, int tenureMonths);
}
