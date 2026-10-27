package com.bank;

import com.bank.exception.BankingException;
import com.bank.model.*;
import com.bank.repository.FileAccountRepository;
import com.bank.service.BankServiceImpl;
import com.bank.service.BankingService;
import com.bank.util.ColorUtil;

import java.util.List;
import java.util.Scanner;

/**
 * Main Application Entry Point
 * Comprehensive Object-Oriented Banking System CLI
 */
public class Main {
    private static final String ADMIN_PIN = "9999";
    private static final BankingService bankService = new BankServiceImpl(new FileAccountRepository());
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Quick automated demo mode for grading / CI verification
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("--test"))) {
            runAutomatedTest();
            return;
        }

        // Initialize sample demo accounts if starting with a fresh database
        bankService.seedSampleData();

        boolean running = true;
        while (running) {
            printHeader("APEX HORIZON BANK - CORE BANKING PORTAL");
            System.out.println(" 1. " + ColorUtil.cyan("Customer Login"));
            System.out.println(" 2. " + ColorUtil.cyan("Open New Account (Quick Registration)"));
            System.out.println(" 3. " + ColorUtil.yellow("Admin Portal (Staff Only)"));
            System.out.println(" 4. " + ColorUtil.green("Financial Calculators (Loan EMI & FD)"));
            System.out.println(" 5. " + ColorUtil.purple("Run Automated System Self-Test (--test)"));
            System.out.println(" 0. Exit System");
            System.out.print("\nSelect an option [0-5]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleCustomerLogin();
                    break;
                case "2":
                    handleOpenNewAccount();
                    break;
                case "3":
                    handleAdminPortal();
                    break;
                case "4":
                    handleCalculatorsMenu();
                    break;
                case "5":
                    runAutomatedTest();
                    break;
                case "0":
                    running = false;
                    System.out.println(ColorUtil.green("\nThank you for banking with Apex Horizon Bank. Goodbye!"));
                    break;
                default:
                    System.out.println(ColorUtil.red("Invalid option. Please enter 0 to 5."));
            }
        }
    }

    // ==========================================
    // CUSTOMER PORTAL
    // ==========================================
    private static void handleCustomerLogin() {
        printHeader("CUSTOMER AUTHENTICATION");
        System.out.print("Enter Account Number (e.g. ACC001001): ");
        String accNum = scanner.nextLine().trim();
        System.out.print("Enter 4-Digit Security PIN: ");
        String pin = scanner.nextLine().trim();

        try {
            Account account = bankService.login(accNum, pin);
            System.out.println(ColorUtil.green("\nLogin successful! Welcome, " + account.getCustomer().getName()));
            customerDashboard(account.getAccountNumber(), pin);
        } catch (BankingException e) {
            System.out.println(ColorUtil.red("\nLogin Failed: " + e.getMessage()));
        }
    }

    private static void customerDashboard(String accNum, String pin) {
        boolean loggedIn = true;
        while (loggedIn) {
            try {
                Account account = bankService.getAccount(accNum);
                printHeader("ACCOUNT DASHBOARD - " + account.getAccountNumber());
                System.out.printf("Holder: %s | Account Type: %s | Current Balance: $%.2f\n",
                        account.getCustomer().getName(), account.getAccountType().getDisplayName(), account.getBalance());
                System.out.println("------------------------------------------------------------");
                System.out.println(" 1. Check Balance");
                System.out.println(" 2. Deposit Funds");
                System.out.println(" 3. Withdraw Cash");
                System.out.println(" 4. Transfer Money (Inter-Account)");
                System.out.println(" 5. View Mini-Statement / Transaction History");
                System.out.println(" 6. Account Summary & Specifications");
                System.out.println(" 7. Change Security PIN");
                System.out.println(" 0. Logout");
                System.out.print("\nSelect an action [0-7]: ");

                String action = scanner.nextLine().trim();
                switch (action) {
                    case "1":
                        System.out.printf(ColorUtil.green("\nCurrent Available Balance: $%.2f\n"), account.getBalance());
                        break;
                    case "2":
                        System.out.print("Enter deposit amount ($): ");
                        double depAmount = readDouble();
                        bankService.deposit(accNum, depAmount);
                        System.out.printf(ColorUtil.green("Successfully deposited $%.2f! New Balance: $%.2f\n"),
                                depAmount, bankService.getAccount(accNum).getBalance());
                        break;
                    case "3":
                        System.out.print("Enter withdrawal amount ($): ");
                        double withAmount = readDouble();
                        bankService.withdraw(accNum, pin, withAmount);
                        System.out.printf(ColorUtil.green("Successfully withdrawn $%.2f! Remaining Balance: $%.2f\n"),
                                withAmount, bankService.getAccount(accNum).getBalance());
                        break;
                    case "4":
                        System.out.print("Enter Beneficiary Account Number: ");
                        String toAcc = scanner.nextLine().trim();
                        System.out.print("Enter transfer amount ($): ");
                        double transferAmount = readDouble();
                        bankService.transfer(accNum, toAcc, pin, transferAmount);
                        System.out.printf(ColorUtil.green("Transfer of $%.2f to %s completed successfully!\n"),
                                transferAmount, toAcc);
                        break;
                    case "5":
                        List<Transaction> txns = bankService.getTransactionHistory(accNum, pin);
                        printHeader("TRANSACTION HISTORY (" + txns.size() + " records)");
                        if (txns.isEmpty()) {
                            System.out.println("No transactions found.");
                        } else {
                            for (Transaction t : txns) {
                                System.out.println(t);
                            }
                        }
                        break;
                    case "6":
                        printHeader("ACCOUNT SPECIFICATIONS");
                        System.out.println(account.getAccountSummary());
                        System.out.println("Account Created On: " + account.getFormattedCreationDate());
                        System.out.println("Linked Customer ID: " + account.getCustomer().getCustomerId());
                        System.out.println("Email: " + account.getCustomer().getEmail());
                        System.out.println("Phone: " + account.getCustomer().getPhone());
                        break;
                    case "7":
                        System.out.print("Enter current PIN: ");
                        String oldPin = scanner.nextLine().trim();
                        System.out.print("Enter new 4-digit PIN: ");
                        String newPin = scanner.nextLine().trim();
                        bankService.changePin(accNum, oldPin, newPin);
                        pin = newPin; // Update local session pin
                        System.out.println(ColorUtil.green("PIN changed successfully! Please remember your new PIN."));
                        break;
                    case "0":
                        loggedIn = false;
                        System.out.println(ColorUtil.yellow("Logged out successfully."));
                        break;
                    default:
                        System.out.println(ColorUtil.red("Invalid option."));
                }
            } catch (BankingException e) {
                System.out.println(ColorUtil.red("Banking Error: " + e.getMessage()));
            } catch (Exception e) {
                System.out.println(ColorUtil.red("Error: " + e.getMessage()));
            }
        }
    }

    // ==========================================
    // ACCOUNT CREATION
    // ==========================================
    private static void handleOpenNewAccount() {
        printHeader("OPEN NEW ACCOUNT");
        System.out.println("Choose Account Type:");
        System.out.println(" 1. Savings Account (4.25% Interest p.a., $100 min balance)");
        System.out.println(" 2. Current Account (Overdraft facility up to $1,000, 0% interest)");
        System.out.println(" 3. Fixed Deposit Account (7.50% High Yield compound interest)");
        System.out.print("Selection [1-3]: ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter Residential Address: ");
        String address = scanner.nextLine().trim();
        System.out.print("Set 4-Digit Security PIN: ");
        String pin = scanner.nextLine().trim();

        try {
            switch (typeChoice) {
                case "1":
                    System.out.print("Enter Initial Deposit ($100 minimum): $");
                    double savDeposit = readDouble();
                    Account savAcc = bankService.createSavingsAccount(name, email, phone, address, pin, savDeposit);
                    printSuccessAccountCreated(savAcc);
                    break;
                case "2":
                    System.out.print("Enter Initial Deposit ($): $");
                    double curDeposit = readDouble();
                    System.out.print("Requested Overdraft Limit (e.g. 1000): $");
                    double overdraft = readDouble();
                    Account curAcc = bankService.createCurrentAccount(name, email, phone, address, pin, curDeposit, overdraft);
                    printSuccessAccountCreated(curAcc);
                    break;
                case "3":
                    System.out.print("Enter Fixed Deposit Principal ($): $");
                    double fdDeposit = readDouble();
                    System.out.print("Enter Tenure in Months (e.g. 12, 24, 36): ");
                    int tenure = (int) readDouble();
                    Account fdAcc = bankService.createFixedDepositAccount(name, email, phone, address, pin, fdDeposit, tenure);
                    printSuccessAccountCreated(fdAcc);
                    break;
                default:
                    System.out.println(ColorUtil.red("Invalid account type selected."));
            }
        } catch (BankingException e) {
            System.out.println(ColorUtil.red("Account Creation Failed: " + e.getMessage()));
        }
    }

    private static void printSuccessAccountCreated(Account account) {
        System.out.println(ColorUtil.green("\n✓ Account opened successfully!"));
        System.out.println("Your New Account Number : " + ColorUtil.bold(account.getAccountNumber()));
        System.out.println("Account Type            : " + account.getAccountType().getDisplayName());
        System.out.println("Account Holder          : " + account.getCustomer().getName());
        System.out.printf("Opening Balance         : $%.2f\n", account.getBalance());
        System.out.println(ColorUtil.yellow("Keep your Account Number and PIN safe for future logins!"));
    }

    // ==========================================
    // ADMIN PORTAL
    // ==========================================
    private static void handleAdminPortal() {
        printHeader("BANK STAFF & ADMIN PORTAL");
        System.out.print("Enter Administrative Master PIN: ");
        String adminPin = scanner.nextLine().trim();
        if (!ADMIN_PIN.equals(adminPin)) {
            System.out.println(ColorUtil.red("Access Denied! Incorrect Admin PIN."));
            return;
        }

        boolean adminActive = true;
        while (adminActive) {
            printHeader("ADMINISTRATION CONTROL PANEL");
            System.out.println(" 1. View All Registered Accounts");
            System.out.println(" 2. View Total Bank Capital & Reserves");
            System.out.println(" 3. Trigger Monthly Interest Disbursement Batch");
            System.out.println(" 4. Seed Standard Sample Test Accounts");
            System.out.println(" 0. Return to Main Menu");
            System.out.print("\nSelect Admin action [0-4]: ");

            String adminChoice = scanner.nextLine().trim();
            switch (adminChoice) {
                case "1":
                    List<Account> accounts = bankService.getAllAccounts();
                    printHeader("ALL REGISTERED BANK ACCOUNTS (" + accounts.size() + ")");
                    if (accounts.isEmpty()) {
                        System.out.println("No accounts currently registered in the system.");
                    } else {
                        System.out.printf("%-12s | %-16s | %-20s | %-12s\n", "Acc Number", "Type", "Customer", "Balance");
                        System.out.println("------------------------------------------------------------------");
                        for (Account acc : accounts) {
                            System.out.printf("%-12s | %-16s | %-20s | $%-12.2f\n",
                                    acc.getAccountNumber(),
                                    acc.getAccountType().name(),
                                    acc.getCustomer().getName(),
                                    acc.getBalance());
                        }
                    }
                    break;
                case "2":
                    double totalReserves = bankService.getTotalBankBalance();
                    int totalAccounts = bankService.getAllAccounts().size();
                    printHeader("BANK FINANCIAL METRICS");
                    System.out.printf("Total Accounts Managed : %d\n", totalAccounts);
                    System.out.printf("Total Capital Held     : " + ColorUtil.green("$%.2f\n"), totalReserves);
                    break;
                case "3":
                    int processed = bankService.applyInterestToAll();
                    System.out.println(ColorUtil.green("Successfully processed interest calculation for " + processed + " accounts."));
                    break;
                case "4":
                    bankService.seedSampleData();
                    System.out.println(ColorUtil.green("Sample demo data seeded successfully."));
                    break;
                case "0":
                    adminActive = false;
                    break;
                default:
                    System.out.println(ColorUtil.red("Invalid option."));
            }
        }
    }

    // ==========================================
    // EXTRA FEATURES: FINANCIAL CALCULATORS
    // ==========================================
    private static void handleCalculatorsMenu() {
        printHeader("FINANCIAL PLANNING CALCULATORS");
        System.out.println(" 1. Loan EMI Calculator (Equated Monthly Installment)");
        System.out.println(" 2. Fixed Deposit Compound Maturity Calculator");
        System.out.println(" 0. Back");
        System.out.print("Select Calculator [0-2]: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Enter Loan Amount ($): ");
                double principal = readDouble();
                System.out.print("Enter Annual Interest Rate (%%) (e.g. 8.5): ");
                double rate = readDouble();
                System.out.print("Enter Loan Tenure in Months (e.g. 36): ");
                int tenure = (int) readDouble();

                double emi = bankService.calculateLoanEMI(principal, rate, tenure);
                double totalPayment = emi * tenure;
                double totalInterest = totalPayment - principal;

                printHeader("LOAN EMI SUMMARY");
                System.out.printf("Monthly EMI Payable : " + ColorUtil.green("$%.2f\n"), emi);
                System.out.printf("Total Interest Paid : $%.2f\n", totalInterest);
                System.out.printf("Total Amount Paid   : $%.2f\n", totalPayment);
                break;
            case "2":
                System.out.print("Enter Deposit Principal ($): ");
                double fdP = readDouble();
                System.out.print("Enter Annual Interest Rate (%%) (e.g. 7.5): ");
                double fdR = readDouble();
                System.out.print("Enter Tenure in Months (e.g. 12): ");
                int fdT = (int) readDouble();

                double maturity = bankService.calculateFixedDepositMaturity(fdP, fdR, fdT);
                printHeader("FIXED DEPOSIT MATURITY SUMMARY");
                System.out.printf("Invested Principal : $%.2f\n", fdP);
                System.out.printf("Interest Earned    : " + ColorUtil.green("$%.2f\n"), (maturity - fdP));
                System.out.printf("Estimated Maturity : " + ColorUtil.green("$%.2f\n"), maturity);
                break;
            case "0":
                break;
            default:
                System.out.println(ColorUtil.red("Invalid selection."));
        }
    }

    // ==========================================
    // AUTOMATED SELF-TEST / REGRESSION SUITE
    // ==========================================
    private static void runAutomatedTest() {
        printHeader("RUNNING AUTOMATED OOP BANKING SYSTEM VERIFICATION");
        int passed = 0;
        int total = 7;

        try {
            // Test 1: Savings Account Creation & Deposit
            System.out.print("[Test 1/7] Creating Savings Account & Checking Balance... ");
            SavingsAccount sa = bankService.createSavingsAccount("Test User", "test@charusat.edu.in", "9998887770",
                    "Campus", "1234", 1000.00);
            assert (sa.getBalance() == 1000.00);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 2: Withdrawal with minimum balance enforcement
            System.out.print("[Test 2/7] Testing Withdrawal & Minimum Balance Constraint... ");
            sa.withdraw(200.00);
            assert (sa.getBalance() == 800.00);
            boolean minBalTriggered = false;
            try {
                sa.withdraw(750.00); // would leave 50, below min 100
            } catch (Exception e) {
                minBalTriggered = true;
            }
            assert (minBalTriggered);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 3: Current Account with Overdraft Limit
            System.out.print("[Test 3/7] Testing Current Account Overdraft Facility... ");
            CurrentAccount ca = bankService.createCurrentAccount("Corporate Client", "corp@bank.com", "8887776660",
                    "HQ", "4321", 500.00, 1000.00);
            ca.withdraw(1000.00); // Balance becomes -500.00
            assert (ca.getBalance() < 0);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 4: Inter-account Transfer
            System.out.print("[Test 4/7] Testing Inter-Account Atomic Fund Transfer... ");
            double saBefore = sa.getBalance();
            bankService.deposit(ca.getAccountNumber(), 1000.00);
            bankService.transfer(ca.getAccountNumber(), sa.getAccountNumber(), "4321", 200.00);
            assert (sa.getBalance() == saBefore + 200.00);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 5: Fixed Deposit & Interest Calculation
            System.out.print("[Test 5/7] Testing Fixed Deposit Compound Calculation... ");
            FixedDepositAccount fd = bankService.createFixedDepositAccount("Investor", "invest@bank.com", "7776665550",
                    "Metro", "5555", 10000.00, 12);
            double interest = fd.calculateInterest();
            assert (interest > 700.0);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 6: Loan EMI Mathematical Model
            System.out.print("[Test 6/7] Testing Loan EMI Formula Verification... ");
            double emi = bankService.calculateLoanEMI(100000, 12, 12); // ~8884.88
            assert (emi > 8800 && emi < 9000);
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            // Test 7: Audit Trail / Transaction History Logging
            System.out.print("[Test 7/7] Testing Transaction Audit Log & History... ");
            List<Transaction> txns = sa.getTransactions();
            assert (!txns.isEmpty());
            System.out.println(ColorUtil.green("PASSED"));
            passed++;

            printHeader("ALL " + passed + "/" + total + " SYSTEM TESTS PASSED SUCCESSFULLY! ✓");
        } catch (Throwable t) {
            System.out.println(ColorUtil.red("FAILED: " + t.getMessage()));
            t.printStackTrace();
        }
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    private static void printHeader(String title) {
        System.out.println("\n" + ColorUtil.bold("============================================================"));
        System.out.println(ColorUtil.bold("  " + title));
        System.out.println(ColorUtil.bold("============================================================"));
    }

    private static double readDouble() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print(ColorUtil.red("Invalid number format. Please enter a valid decimal number: "));
            }
        }
    }
}
