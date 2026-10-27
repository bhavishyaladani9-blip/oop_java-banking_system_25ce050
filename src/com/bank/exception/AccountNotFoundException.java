package com.bank.exception;

/**
 * Thrown when an account lookup fails.
 */
public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found with account number: " + accountNumber);
    }
}
