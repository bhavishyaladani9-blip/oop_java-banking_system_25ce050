package com.bank.util;

/**
 * Utility class for common input validations.
 */
public final class ValidationUtil {
    private ValidationUtil() {}

    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("^\\d{4}$");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return phone.matches("^[+0-9\\-\\s()]{7,18}$");
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }
}
