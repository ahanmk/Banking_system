package model;

public class CurrentAccount extends Account {
    public static final double OVERDRAFT_LIMIT = 5000;

    public CurrentAccount(String accountNumber, String holderName, double openingBalance) {
        super(accountNumber, holderName, openingBalance);
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return getBalance() - amount >= -OVERDRAFT_LIMIT;
    }

    @Override
    protected String getRuleDescription() {
        return "Current accounts have an overdraft limit of Rs. " + (int) OVERDRAFT_LIMIT + ".";
    }

    @Override
    public String getAccountType() { return "Current"; }
}