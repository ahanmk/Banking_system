package model;

import exception.InsufficientFundsException;
import java.util.ArrayList;
public abstract class Account {
    private final String accountNumber;
    private final String holderName;
    private double balance;
    private final ArrayList<String> history = new ArrayList<>();

    public Account(String accountNumber, String holderName, double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = openingBalance;
        history.add(String.format("Account opened with Rs. %.2f", openingBalance));
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public ArrayList<String> getHistory() { return new ArrayList<>(history); }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        history.add(String.format("Deposited Rs. %.2f | Balance: Rs. %.2f", amount, balance));
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException("Withdrawal not allowed. " + getRuleDescription());
        }
        balance -= amount;
        history.add(String.format("Withdrew Rs. %.2f | Balance: Rs. %.2f", amount, balance));
    }

    // Each account type decides its own rule (polymorphism)
    protected abstract boolean canWithdraw(double amount);

    protected abstract String getRuleDescription();

    public abstract String getAccountType();

    public String getDetails() {
        return String.format("%s | %-8s | %-15s | Rs. %.2f",
                accountNumber, getAccountType(), holderName, balance);
    }
}