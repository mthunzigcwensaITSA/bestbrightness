package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.db.DatabaseManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class InitialSetupFrame extends JFrame {

    private final DatabaseManager databaseManager;
    private final Runnable onSetupComplete;
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();

    public InitialSetupFrame(DatabaseManager databaseManager, Runnable onSetupComplete) {
        this.databaseManager = databaseManager;
        this.onSetupComplete = onSetupComplete;
        initialize();
    }

    private void initialize() {
        setTitle("Best Brightness POS - First-Time Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 420));

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel formCard = UiTheme.createCard();
        formCard.setLayout(new BorderLayout(0, 18));
        formCard.setPreferredSize(new Dimension(640, 0));

        UiTheme.styleField(passwordField);
        UiTheme.styleField(confirmField);
        passwordField.putClientProperty("JTextField.placeholderText", "Create admin password");
        confirmField.putClientProperty("JTextField.placeholderText", "Confirm admin password");
        passwordField.addActionListener(event -> createAdmin());
        confirmField.addActionListener(event -> createAdmin());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(UiTheme.createTitleLabel("Set up Best Brightness"));
        top.add(Box.createVerticalStrut(8));
        top.add(UiTheme.createInfoText(
                "Create the first admin password here. The username stays fixed as admin for the first sign-in."));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 8, 14);
        formPanel.add(UiTheme.createMutedLabel("Password"), constraints);
        constraints.gridx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 8, 0);
        formPanel.add(UiTheme.createMutedLabel("Confirm password"), constraints);
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.insets = new java.awt.Insets(0, 0, 0, 14);
        formPanel.add(passwordField, constraints);
        constraints.gridx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 0, 0);
        formPanel.add(confirmField, constraints);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        JButton createButton = UiTheme.createPrimaryButton("Create Admin Account");
        createButton.addActionListener(event -> createAdmin());
        JButton exitButton = UiTheme.createSecondaryButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));
        buttonRow.add(createButton);
        buttonRow.add(exitButton);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(formPanel);
        center.add(Box.createVerticalStrut(18));
        center.add(buttonRow);

        formCard.add(top, BorderLayout.NORTH);
        formCard.add(center, BorderLayout.CENTER);

        root.add(formCard);
        setContentPane(root);
        setSize(UiTheme.fitToScreen(760, 430));
        setLocationRelativeTo(null);
    }

    private void createAdmin() {
        char[] password = passwordField.getPassword();
        char[] confirmation = confirmField.getPassword();
        try {
            if (password.length == 0 || isBlank(password)) {
                JOptionPane.showMessageDialog(this, "Admin password cannot be blank.", "Setup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Arrays.equals(password, confirmation)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Setup Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            databaseManager.createInitialAdmin(password);
            JOptionPane.showMessageDialog(this, "Admin account created successfully.");
            dispose();
            onSetupComplete.run();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Setup Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
            passwordField.setText("");
            confirmField.setText("");
        }
    }

    private boolean isBlank(char[] value) {
        for (char character : value) {
            if (!Character.isWhitespace(character)) {
                return false;
            }
        }
        return true;
    }
}
