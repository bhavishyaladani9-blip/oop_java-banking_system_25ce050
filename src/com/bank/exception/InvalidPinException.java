package com.bank.exception;

/**
 * Thrown when an invalid PIN is supplied during authentication.
 */
public class InvalidPinException extends BankingException {
    public InvalidPinException() {
        super("Invalid PIN! Access denied.");
    }

    public InvalidPinException(String message) {
        super(message);
    }
}
