package model;

public class SavingsAccount extends Account {
    public static final double MIN_BALANCE = 500;
    public static final double INTEREST_RATE = 4.0;   // percent

    public SavingsAccount(String accountNumber, String holderName, double openingBalance) {
        super(accountNumber, holderName, openingBalance);
        if (openingBalance < MIN_BALANCE) {
            throw new IllegalArgumentException("Savings account needs at least Rs. " + (int) MIN_BALANCE + " to open.");
        }
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return getBalance() - amount >= MIN_BALANCE;
    }

    @Override
    protected String getRuleDescription() {
        return "Savings accounts must keep a minimum balance of Rs. " + (int) MIN_BALANCE + ".";
    }

    @Override
    public String getAccountType() { return "Savings"; }

    public void addInterest() {
        double interest = getBalance() * INTEREST_RATE / 100;
        if (interest <= 0) {
            throw new IllegalArgumentException("No interest to add.");
        }
        deposit(interest);
    }
}