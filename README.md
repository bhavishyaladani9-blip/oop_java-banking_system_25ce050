# 🏦 Core Banking Management System (OOP Java)

[![Java Version](https://img.shields.io/badge/Java-11%2B%20%7C%2021%2B-orange.svg)](https://www.oracle.com/java/)
[![OOP Principles](https://img.shields.io/badge/Paradigm-Object--Oriented%20Programming-blue.svg)]()
[![Build Status](https://img.shields.io/badge/Tests-7%2F7%20Passed-brightgreen.svg)]()
[![Repository](https://img.shields.io/badge/GitHub-25CE050-informational.svg)](https://github.com/bhavishyaladani9-blip/oop_java-banking_system_25ce050)

A robust, enterprise-grade Core Banking Console Application engineered in pure Java, strictly adhering to **Object-Oriented Programming (OOP)** design patterns and software engineering best practices.

Developed for academic evaluation & portfolio demonstration by **25CE050**.

---

## 🌐 GitHub Repository URL

```
https://github.com/bhavishyaladani9-blip/oop_java-banking_system_25ce050
```

---

## ⚡ Quick Start (Cloning & Running)

Anyone can clone and run this project immediately via the terminal without needing Maven or Gradle:

### 1. Clone the repository
```bash
git clone https://github.com/bhavishyaladani9-blip/oop_java-banking_system_25ce050.git
cd oop_java-banking_system_25ce050
```

### 2. Run the application

#### On Windows:
```cmd
run.bat
```
*(Or in PowerShell: `.\run.bat`)*

#### On Linux / macOS:
```bash
chmod +x run.sh
./run.sh
```

#### Manual Compilation & Run (Any Platform):
```bash
# 1. Create output directory
mkdir bin

# 2. Compile all source files
javac -d bin src/com/bank/model/*.java src/com/bank/service/*.java src/com/bank/repository/*.java src/com/bank/exception/*.java src/com/bank/util/*.java src/com/bank/Main.java

# 3. Launch the application
java -cp bin com.bank.Main
```

#### Run Automated Self-Test Suite:
```bash
java -cp bin com.bank.Main --test
```

---

## 🏛️ OOP Principles Demonstrated

| OOP Principle | Implementation in Codebase |
| :--- | :--- |
| **Abstraction** | Abstract base class [`Account`](src/com/bank/model/Account.java) defines contract without exposing internal mechanics. Interfaces [`BankingService`](src/com/bank/service/BankingService.java) and [`AccountRepository`](src/com/bank/repository/AccountRepository.java) isolate business and storage layers. |
| **Encapsulation** | Strict `private` and `protected` fields with regulated getters and setters. Defensive copying of collections and state validation to prevent corruption. |
| **Inheritance** | Class hierarchy where [`SavingsAccount`](src/com/bank/model/SavingsAccount.java), [`CurrentAccount`](src/com/bank/model/CurrentAccount.java), and [`FixedDepositAccount`](src/com/bank/model/FixedDepositAccount.java) inherit standard account state from `Account`. |
| **Polymorphism** | Dynamic dispatch of polymorphic methods: `withdraw()`, `calculateInterest()`, `applyInterest()`, and `getAccountSummary()` dynamically resolve to subclass behavior at runtime. |

---

## 🚀 Key & Extra Features

### 1. Multi-Tier Account System
- **Savings Account**: 
  - Compound annual interest (4.25% p.a.).
  - Minimum balance guard ($100.00).
  - Daily withdrawal limit safeguard.
- **Current / Checking Account**:
  - Overdraft credit limit protection ($1,000+).
  - Configurable transaction processing fees.
- **Fixed Deposit Account**:
  - Term-locked deposit with high yield (7.50% p.a.).
  - Early liquidation penalty calculation.

### 2. Transaction Engine & Audit Trail
- **Inter-Account Money Transfer**: Atomic transfer with deadlock-free resource ordering.
- **Mini-Statements**: Timestamped audit log with transaction ID, type, reference account, and post-transaction balance.

### 3. Data Persistence Layer
- Automatic object serialization to `data/accounts.dat`. Bank accounts, balances, and transaction histories persist across restarts.

### 4. 🌟 Extra Specialized Features
1. **Financial Calculators**:
   - **Loan EMI Calculator**: Equated Monthly Installment calculation using compound amortization formulas:
     $$EMI = \frac{P \times r \times (1+r)^n}{(1+r)^n - 1}$$
   - **Fixed Deposit Compound Maturity Estimator**: Quarterly compounding interest forecast.
2. **Dual-Role Access Control**:
   - **Customer Portal**: Account access with 4-digit PIN authentication.
   - **Admin / Staff Portal**: Master PIN (`9999`) to audit total bank capital, view all accounts, and execute batch interest disbursements.
3. **Automated Verification Suite (`--test`)**:
   - 7 automated regression tests verifying accounts, constraints, overdrafts, transfers, and calculations.
4. **Rich Terminal UI**:
   - ANSI color formatting for feedback, alerts, and tables.

---

## 📂 Project Architecture

```
oop_java-banking_system_25ce050/
├── .gitignore                      # Git ignore rules for compiled classes & data
├── README.md                       # Complete documentation & usage guide
├── run.bat                         # Windows 1-click build and execution script
├── run.sh                          # Linux/macOS build and execution script
├── src/
│   └── com/
│       └── bank/
│           ├── Main.java           # Terminal CLI interface & entry point
│           ├── exception/          # Custom exceptions
│           │   ├── BankingException.java
│           │   ├── AccountNotFoundException.java
│           │   ├── InsufficientFundsException.java
│           │   └── InvalidPinException.java
│           ├── model/              # Domain models & OOP hierarchy
│           │   ├── Account.java            (Abstract Base Class)
│           │   ├── SavingsAccount.java     (Extends Account)
│           │   ├── CurrentAccount.java     (Extends Account)
│           │   ├── FixedDepositAccount.java(Extends Account)
│           │   ├── Customer.java
│           │   ├── Transaction.java
│           │   ├── TransactionType.java
│           │   └── AccountType.java
│           ├── repository/         # Data persistence layer
│           │   ├── AccountRepository.java  (Interface)
│           │   └── FileAccountRepository.java (Serialization)
│           ├── service/            # Core business logic
│           │   ├── BankingService.java     (Interface)
│           │   └── BankServiceImpl.java
│           └── util/               # Helper utilities
│               ├── ColorUtil.java
│               └── ValidationUtil.java
└── data/                           # Serialized data storage (auto-created)
```

---

## 🔐 Default Demo Accounts (Pre-seeded)

| Account Number | Account Type | Holder | PIN | Initial Balance | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `ACC001001` | Savings | Alice Sharma | `1111` | $5,000.00 | Min Balance: $100 |
| `ACC001002` | Current | Bob Patel | `2222` | $12,500.00 | Overdraft: $5,000 |
| `ACC001003` | Fixed Deposit | Charlie Mehta | `3333` | $25,000.00 | 12-Month Term |

*Staff / Admin Master PIN: `9999`*

---

## 🧪 Automated Test Execution Output

```
============================================================
  RUNNING AUTOMATED OOP BANKING SYSTEM VERIFICATION
============================================================
[Test 1/7] Creating Savings Account & Checking Balance... PASSED
[Test 2/7] Testing Withdrawal & Minimum Balance Constraint... PASSED
[Test 3/7] Testing Current Account Overdraft Facility... PASSED
[Test 4/7] Testing Inter-Account Atomic Fund Transfer... PASSED
[Test 5/7] Testing Fixed Deposit Compound Calculation... PASSED
[Test 6/7] Testing Loan EMI Formula Verification... PASSED
[Test 7/7] Testing Transaction Audit Log & History... PASSED

============================================================
  ALL 7/7 SYSTEM TESTS PASSED SUCCESSFULLY! ✓
============================================================
```

---

## 👤 Author
- **Student ID**: 25CE050
- **GitHub**: [@bhavishyaladani9-blip](https://github.com/bhavishyaladani9-blip)
- **Repo**: [oop_java-banking_system_25ce050](https://github.com/bhavishyaladani9-blip/oop_java-banking_system_25ce050)
