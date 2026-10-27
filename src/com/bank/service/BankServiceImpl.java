package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidPinException;
import com.bank.model.*;
import com.bank.repository.AccountRepository;
import com.bank.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

/**
 * Core implementation of the BankingService interface.
 * Implements transaction coordination, atomicity, thread-safety, and input validation.
 */
public class BankServiceImpl implements BankingService {
    private final AccountRepository repository;

    public BankServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public SavingsAccount createSavingsAccount(String customerName, String email, String phone, String address,
                                               String pin, double initialDeposit) throws BankingException {
        validateCustomerInput(customerName, pin, initialDeposit);
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer customer = new Customer(customerId, customerName, email, phone, address);

        String accNumber = repository.generateNextAccountNumber();
        SavingsAccount account = new SavingsAccount(accNumber, customer, pin, initialDeposit);
        repository.save(account);
        return account;
    }

    @Override
    public CurrentAccount createCurrentAccount(String customerName, String email, String phone, String address,
                                               String pin, double initialDeposit, double overdraftLimit) throws BankingException {
        validateCustomerInput(customerName, pin, initialDeposit);
        if (overdraftLimit < 0) {
            throw new BankingException("Overdraft limit cannot be negative.");
        }
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer customer = new Customer(customerId, customerName, email, phone, address);

        String accNumber = repository.generateNextAccountNumber();
        CurrentAccount account = new CurrentAccount(accNumber, customer, pin, initialDeposit, overdraftLimit, 0.50);
        repository.save(account);
        return account;
    }

    @Override
    public FixedDepositAccount createFixedDepositAccount(String customerName, String email, String phone, String address,
                                                         String pin, double depositAmount, int termMonths) throws BankingException {
        validateCustomerInput(customerName, pin, depositAmount);
        if (termMonths < 1) {
            throw new BankingException("Term duration must be at least 1 month.");
        }
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer customer = new Customer(customerId, customerName, email, phone, address);

        String accNumber = repository.generateNextAccountNumber();
        FixedDepositAccount account = new FixedDepositAccount(accNumber, customer, pin, depositAmount, termMonths);
        repository.save(account);
        return account;
    }

    @Override
    public Account login(String accountNumber, String pin) throws BankingException {
        Account account = getAccount(accountNumber);
        if (!account.authenticate(pin)) {
            throw new InvalidPinException();
        }
        return account;
    }

    @Override
    public void changePin(String accountNumber, String oldPin, String newPin) throws BankingException {
        Account account = getAccount(accountNumber);
        account.changePin(oldPin, newPin);
        repository.persistChanges();
    }

    @Override
    public Account getAccount(String accountNumber) throws BankingException {
        return repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Override
    public void deposit(String accountNumber, double amount) throws BankingException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankingException("Deposit amount must be a positive number.");
        }
        Account account = getAccount(accountNumber);
        account.deposit(amount);
        repository.persistChanges();
    }

    @Override
    public void withdraw(String accountNumber, String pin, double amount) throws BankingException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankingException("Withdrawal amount must be a positive number.");
        }
        Account account = login(accountNumber, pin);
        account.withdraw(amount);
        repository.persistChanges();
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, String pin, double amount) throws BankingException {
        if (fromAccountNumber == null || toAccountNumber == null) {
            throw new BankingException("Account numbers cannot be null.");
        }
        if (fromAccountNumber.trim().equalsIgnoreCase(toAccountNumber.trim())) {
            throw new BankingException("Cannot transfer money to the same account.");
        }
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankingException("Transfer amount must be a positive number.");
        }

        Account source = login(fromAccountNumber, pin);
        Account target = getAccount(toAccountNumber);

        // Deadlock-free locking order based on account number
        Account firstLock = source.getAccountNumber().compareTo(target.getAccountNumber()) < 0 ? source : target;
        Account secondLock = firstLock == source ? target : source;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Withdraw from source
                source.withdraw(amount);

                // Credit target directly without triggering regular cash deposit log
                target.deposit(amount);

                // Record transfer-specific transaction logs on both ends
                Transaction outTxn = new Transaction(
                        TransactionType.TRANSFER_OUT,
                        amount,
                        source.getBalance(),
                        String.format("Transfer to %s (%s)", target.getAccountNumber(), target.getCustomer().getName()),
                        target.getAccountNumber()
                );
                source.addTransaction(outTxn);

                Transaction inTxn = new Transaction(
                        TransactionType.TRANSFER_IN,
                        amount,
                        target.getBalance(),
                        String.format("Transfer from %s (%s)", source.getAccountNumber(), source.getCustomer().getName()),
                        source.getAccountNumber()
                );
                target.addTransaction(inTxn);

                repository.persistChanges();
            }
        }
    }

    @Override
    public double getBalance(String accountNumber, String pin) throws BankingException {
        Account account = login(accountNumber, pin);
        return account.getBalance();
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber, String pin) throws BankingException {
        Account account = login(accountNumber, pin);
        return account.getTransactions();
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    @Override
    public double getTotalBankBalance() {
        return repository.findAll().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    @Override
    public int applyInterestToAll() {
        List<Account> accounts = repository.findAll();
        int processed = 0;
        for (Account account : accounts) {
            if (account instanceof SavingsAccount || account instanceof FixedDepositAccount) {
                account.applyInterest();
                processed++;
            }
        }
        repository.persistChanges();
        return processed;
    }

    @Override
    public void seedSampleData() {
        if (!repository.findAll().isEmpty()) {
            return; // Already populated
        }
        try {
            createSavingsAccount("Alice Sharma", "alice@example.com", "+91-9876543210",
                    "A-102 Sky Tower, Ahmedabad", "1111", 5000.00);
            createCurrentAccount("Bob Patel", "bob@business.com", "+91-9823456789",
                    "405 Commerce Hub, Vadodara", "2222", 12500.00, 5000.00);
            createFixedDepositAccount("Charlie Mehta", "charlie@gmail.com", "+91-9123456780",
                    "78 Green Meadows, Anand", "3333", 25000.00, 12);
        } catch (BankingException e) {
            System.err.println("Seeding error: " + e.getMessage());
        }
    }

    @Override
    public double calculateLoanEMI(double principal, double annualRatePercentage, int tenureMonths) {
        if (principal <= 0 || annualRatePercentage <= 0 || tenureMonths <= 0) {
            return 0.0;
        }
        double monthlyRate = (annualRatePercentage / 12.0) / 100.0;
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths))
                / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return emi;
    }

    @Override
    public double calculateFixedDepositMaturity(double principal, double annualRatePercentage, int tenureMonths) {
        if (principal <= 0 || annualRatePercentage <= 0 || tenureMonths <= 0) {
            return 0.0;
        }
        // Quarterly compounded
        double quarterlyRate = (annualRatePercentage / 4.0) / 100.0;
        double quarters = (tenureMonths / 12.0) * 4.0;
        return principal * Math.pow(1 + quarterlyRate, quarters);
    }

    private void validateCustomerInput(String name, String pin, double initialAmount) throws BankingException {
        if (name == null || name.trim().length() < 2) {
            throw new BankingException("Customer name must be at least 2 characters long.");
        }
        if (!ValidationUtil.isValidPin(pin)) {
            throw new BankingException("PIN must be exactly 4 numeric digits.");
        }
        if (initialAmount < 0) {
            throw new BankingException("Deposit amount cannot be negative.");
        }
    }
}
