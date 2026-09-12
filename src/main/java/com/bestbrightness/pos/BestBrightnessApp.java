package com.bestbrightness.pos;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.service.AuthService;
import com.bestbrightness.pos.service.PosService;
import com.bestbrightness.pos.ui.LoginFrame;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

public final class BestBrightnessApp {

    private BestBrightnessApp() {
    }

    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager(Path.of("bestbrightness.db"));
            databaseManager.initializeDatabase();
            if (!ensureAdminUser(databaseManager)) {
                return;
            }
            AuthService authService = new AuthService(databaseManager);
            PosService posService = new PosService(databaseManager);

            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("Best Brightness POS database initialized.");
                return;
            }

            SwingUtilities.invokeLater(() -> new LoginFrame(authService, posService).setVisible(true));
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean ensureAdminUser(DatabaseManager databaseManager) throws Exception {
        if (databaseManager.hasUsers()) {
            return true;
        }

        if (GraphicsEnvironment.isHeadless()) {
            String adminPassword = System.getenv("BEST_BRIGHTNESS_ADMIN_PASSWORD");
            if (adminPassword == null || adminPassword.isBlank()) {
                System.out.println("No users configured. Set BEST_BRIGHTNESS_ADMIN_PASSWORD and rerun.");
                return false;
            }
            databaseManager.createInitialAdmin(adminPassword);
            return true;
        }

        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        Object[] message = {
                "Set the initial admin password:", passwordField,
                "Confirm password:", confirmField
        };

        while (true) {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "First-Time Setup",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return false;
            }

            String password = new String(passwordField.getPassword());
            String confirmation = new String(confirmField.getPassword());
            if (password.isBlank()) {
                JOptionPane.showMessageDialog(null, "Admin password cannot be blank.");
                continue;
            }
            if (!password.equals(confirmation)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match.");
                continue;
            }

            databaseManager.createInitialAdmin(password);
            return true;
        }
    }
}
