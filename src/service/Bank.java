package service;

import exception.InsufficientFundsException;
import model.*;
import java.util.ArrayList;

public class Bank {
    private final ArrayList<Account> accounts = new ArrayList<>();
    private int nextNumber = 1001;

    public Account openAccount(String type, String holderName, double openingBalance) {
        String number = "AC" + nextNumber;
        Account account;
        if (type.equals("Savings")) {
            account = new SavingsAccount(number, holderName, openingBalance);
        } else {
            account = new CurrentAccount(number, holderName, openingBalance);
        }
        nextNumber++;                  // only used up if creation succeeded
        accounts.add(account);
        return account;
    }

    public Account find(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                return a;
            }
        }
        throw new IllegalArgumentException("Account " + accountNumber.trim() + " not found.");
    }

    public void transfer(String fromNumber, String toNumber, double amount)
            throws InsufficientFundsException {
        Account from = find(fromNumber);
        Account to = find(toNumber);
        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        from.withdraw(amount);         // throws if the rule is broken, so nothing is deposited
        to.deposit(amount);
    }

    public ArrayList<Account> getAccounts() { return accounts; }
}