package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.db.DatabaseManager;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

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
        setMinimumSize(new java.awt.Dimension(420, 520));

        JPanel content = new JPanel();
        content.setBackground(UiTheme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel heroCard = UiTheme.createCard();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroCard.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 220));
        heroCard.add(UiTheme.createTitleLabel("Set up Best Brightness"));
        heroCard.add(Box.createVerticalStrut(10));
        heroCard.add(UiTheme.createInfoText(
                "Create the first admin password in this window before signing in to the point of sale system."));

        JPanel formCard = UiTheme.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 280));

        UiTheme.styleField(passwordField);
        UiTheme.styleField(confirmField);
        passwordField.putClientProperty("JTextField.placeholderText", "Create admin password");
        confirmField.putClientProperty("JTextField.placeholderText", "Confirm admin password");
        passwordField.addActionListener(event -> createAdmin());
        confirmField.addActionListener(event -> createAdmin());

        formCard.add(UiTheme.createSectionLabel("Admin credentials"));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(UiTheme.createInfoText("The username is fixed as admin for the first sign-in."));
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createFieldBlock("Password", passwordField));
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(createFieldBlock("Confirm password", confirmField));
        formCard.add(Box.createVerticalStrut(18));

        JPanel buttonRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        JButton createButton = UiTheme.createPrimaryButton("Create Admin Account");
        createButton.addActionListener(event -> createAdmin());
        JButton exitButton = UiTheme.createSecondaryButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));
        buttonRow.add(createButton);
        buttonRow.add(exitButton);
        formCard.add(buttonRow);

        content.add(heroCard);
        content.add(Box.createVerticalStrut(18));
        content.add(formCard);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);
        setSize(UiTheme.fitToScreen(720, 700));
        setLocationRelativeTo(null);
    }

    private JPanel createFieldBlock(String labelText, JPasswordField field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(UiTheme.createMutedLabel(labelText));
        panel.add(Box.createVerticalStrut(6));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        return panel;
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
