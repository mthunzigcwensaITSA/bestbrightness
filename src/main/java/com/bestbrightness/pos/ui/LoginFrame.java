package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.User;
import com.bestbrightness.pos.service.AuthService;
import com.bestbrightness.pos.service.PosService;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final PosService posService;
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame(AuthService authService, PosService posService) {
        this.authService = authService;
        this.posService = posService;
        initialize();
    }

    private void initialize() {
        setTitle("Best Brightness POS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 520));

        JPanel root = new JPanel();
        root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(createBrandPanel());
        root.add(Box.createVerticalStrut(18));
        root.add(createLoginPanel());

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);
        setSize(UiTheme.fitToScreen(760, 760));
        setLocationRelativeTo(null);
    }

    private JPanel createBrandPanel() {
        JPanel panel = UiTheme.createCard();
        panel.setBackground(UiTheme.SURFACE_ALT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel badge = new JLabel("BB");
        badge.setOpaque(true);
        badge.setBackground(UiTheme.ACCENT);
        badge.setForeground(UiTheme.BACKGROUND);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 34f));
        badge.setMaximumSize(new Dimension(88, 88));
        badge.setPreferredSize(new Dimension(88, 88));
        badge.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = UiTheme.createTitleLabel("Best Brightness");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        javax.swing.JTextArea subtitle = UiTheme.createInfoText(
                "Modern point of sale operations for cleaning products, cashier workflows, and live stock control.");
        subtitle.setFont(subtitle.getFont().deriveFont(15f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(badge);
        panel.add(Box.createVerticalStrut(24));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalGlue());
        panel.add(createFeature("Secure sign-in", "Protected admin access with hashed credentials"));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFeature("Fast checkout", "Cart totals, discounts, and receipts in one flow"));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFeature("Live inventory", "Stock updates immediately after completed sales"));
        return panel;
    }

    private JPanel createFeature(String title, String description) {
        JPanel feature = new JPanel();
        feature.setLayout(new BoxLayout(feature, BoxLayout.Y_AXIS));
        feature.setBackground(UiTheme.SURFACE);
        feature.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        feature.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLabel = UiTheme.createSectionLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        javax.swing.JTextArea descriptionLabel = UiTheme.createInfoText(description);
        feature.add(titleLabel);
        feature.add(Box.createVerticalStrut(4));
        feature.add(descriptionLabel);
        return feature;
    }

    private JPanel createLoginPanel() {
        JPanel panel = UiTheme.createCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));

        JLabel overline = UiTheme.createMutedLabel("WELCOME BACK");
        overline.setFont(overline.getFont().deriveFont(Font.BOLD, 13f));
        overline.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = UiTheme.createTitleLabel("Sign in to your workspace");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        javax.swing.JTextArea subtitle = UiTheme.createInfoText(
                "Manage products, process sales, and track receipts from a polished desktop experience.");
        subtitle.setFont(subtitle.getFont().deriveFont(15f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        UiTheme.styleField(usernameField);
        UiTheme.styleField(passwordField);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        usernameField.addActionListener(event -> handleLogin());
        passwordField.addActionListener(event -> handleLogin());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 8, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        formPanel.add(UiTheme.createMutedLabel("Username"), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 18, 0);
        formPanel.add(usernameField, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 8, 0);
        formPanel.add(UiTheme.createMutedLabel("Password"), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 18, 0);
        formPanel.add(passwordField, constraints);

        JButton loginButton = UiTheme.createPrimaryButton("Open POS Dashboard");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(event -> handleLogin());

        JButton clearButton = UiTheme.createSecondaryButton("Clear");
        clearButton.addActionListener(event -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(loginButton);
        buttonRow.add(clearButton);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = UiTheme.createMutedLabel("Use the admin username with the password created during first-time setup.");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(overline);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(formPanel);
        panel.add(buttonRow);
        panel.add(Box.createVerticalGlue());
        panel.add(hint);

        return panel;
    }

    private void handleLogin() {
        char[] password = passwordField.getPassword();
        try {
            User user = authService.authenticate(usernameField.getText(), password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
            new MainFrame(user, posService).setVisible(true);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}
