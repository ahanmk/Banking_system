package gui;

import exception.InsufficientFundsException;
import model.*;
import service.Bank;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    // Small interface so every button can share one try/catch (see perform)
    private interface Task {
        void run() throws InsufficientFundsException;
    }

    private final Bank bank = new Bank();

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Savings", "Current"});
    private final JTextField nameField = new JTextField(10);
    private final JTextField openingField = new JTextField(7);
    private final JTextField accNoField = new JTextField(7);
    private final JTextField amountField = new JTextField(7);
    private final JTextField toAccField = new JTextField(7);
    private final JTextArea display = new JTextArea();

    public MainWindow() {
        setTitle("Bank");
        setSize(760, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Panel 1: open account
        JPanel openPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openPanel.setBorder(BorderFactory.createTitledBorder("Open account"));
        openPanel.add(typeBox);
        openPanel.add(new JLabel("Holder name:"));
        openPanel.add(nameField);
        openPanel.add(new JLabel("Opening balance:"));
        openPanel.add(openingField);
        JButton openButton = new JButton("Open Account");
        openPanel.add(openButton);

        // Panel 2: deposit / withdraw / history / interest
        JPanel txPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        txPanel.add(new JLabel("Account no:"));
        txPanel.add(accNoField);
        txPanel.add(new JLabel("Amount:"));
        txPanel.add(amountField);
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton historyButton = new JButton("History");
        JButton interestButton = new JButton("Add Interest");
        txPanel.add(depositButton);
        txPanel.add(withdrawButton);
        txPanel.add(historyButton);
        txPanel.add(interestButton);

        // Panel 3: transfer
        JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transferPanel.setBorder(BorderFactory.createTitledBorder("Transfer (from the account number above)"));
        transferPanel.add(new JLabel("To account:"));
        transferPanel.add(toAccField);
        JButton transferButton = new JButton("Transfer");
        transferPanel.add(transferButton);

        JPanel top = new JPanel(new GridLayout(3, 1, 5, 5));
        top.add(openPanel);
        top.add(txPanel);
        top.add(transferPanel);
        add(top, BorderLayout.NORTH);

        display.setEditable(false);
        display.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(display);
        scroll.setBorder(BorderFactory.createTitledBorder("All accounts"));
        add(scroll, BorderLayout.CENTER);

        // Events (each button handler is a lambda)
        openButton.addActionListener(e -> perform(() -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Enter the holder name.");
            }
            double opening = Double.parseDouble(openingField.getText().trim());
            Account a = bank.openAccount((String) typeBox.getSelectedItem(), name, opening);
            message("Account created. Account number: " + a.getAccountNumber());
            nameField.setText("");
            openingField.setText("");
        }));

        depositButton.addActionListener(e -> perform(() -> {
            bank.find(accNoField.getText()).deposit(readAmount());
            amountField.setText("");
        }));

        withdrawButton.addActionListener(e -> perform(() -> {
            bank.find(accNoField.getText()).withdraw(readAmount());
            amountField.setText("");
        }));

        transferButton.addActionListener(e -> perform(() -> {
            bank.transfer(accNoField.getText(), toAccField.getText(), readAmount());
            message("Transfer successful.");
            amountField.setText("");
        }));

        historyButton.addActionListener(e -> perform(() -> {
            Account a = bank.find(accNoField.getText());
            JTextArea area = new JTextArea(String.join("\n", a.getHistory()), 12, 40);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "History - " + a.getAccountNumber(), JOptionPane.PLAIN_MESSAGE);
        }));

        interestButton.addActionListener(e -> perform(() -> {
            Account a = bank.find(accNoField.getText());
            if (a instanceof SavingsAccount) {
                ((SavingsAccount) a).addInterest();
            } else {
                throw new IllegalArgumentException("Interest applies only to Savings accounts.");
            }
        }));

        refresh();
    }

    /** Runs a button's action and turns every error into a friendly message. */
    private void perform(Task task) {
        try {
            task.run();
            refresh();
        } catch (NumberFormatException ex) {
            message("Please enter a valid number.");
        } catch (InsufficientFundsException | IllegalArgumentException ex) {
            message(ex.getMessage());
        }
    }

    private double readAmount() {
        return Double.parseDouble(amountField.getText().trim());
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private void refresh() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s | %-8s | %-15s | %s%n", "No", "Type", "Holder", "Balance"));
        sb.append("-".repeat(55)).append("\n");
        for (Account a : bank.getAccounts()) {
            sb.append(a.getDetails()).append("\n");
        }
        display.setText(sb.toString());
    }
}