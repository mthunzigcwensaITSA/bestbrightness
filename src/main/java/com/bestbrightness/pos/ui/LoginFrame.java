package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.User;
import com.bestbrightness.pos.service.AuthService;
import com.bestbrightness.pos.service.PosService;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
        setSize(360, 220);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> handleLogin());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        try {
            User user = authService.authenticate(usernameField.getText(), new String(passwordField.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
            new MainFrame(user, posService).setVisible(true);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
