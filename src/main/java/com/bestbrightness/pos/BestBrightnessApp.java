package com.bestbrightness.pos;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.service.AuthService;
import com.bestbrightness.pos.service.PosService;
import com.bestbrightness.pos.ui.InitialSetupFrame;
import com.bestbrightness.pos.ui.LoginFrame;
import com.bestbrightness.pos.ui.UiTheme;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import javax.swing.SwingUtilities;

public final class BestBrightnessApp {

    private BestBrightnessApp() {
    }

    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager(Path.of("bestbrightness.db"));
            databaseManager.initializeDatabase();
            boolean hasUsers = databaseManager.hasUsers();
            if (GraphicsEnvironment.isHeadless()) {
                if (!ensureHeadlessAdminUser(databaseManager, hasUsers)) {
                    return;
                }
                System.out.println("Best Brightness POS database initialized.");
                return;
            }

            AuthService authService = new AuthService(databaseManager);
            PosService posService = new PosService(databaseManager);
            UiTheme.setup();
            SwingUtilities.invokeLater(() -> launchUi(hasUsers, databaseManager, authService, posService));
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static void launchUi(boolean hasUsers, DatabaseManager databaseManager, AuthService authService,
                                 PosService posService) {
        if (hasUsers) {
            new LoginFrame(authService, posService).setVisible(true);
            return;
        }

        new InitialSetupFrame(databaseManager, () -> new LoginFrame(authService, posService).setVisible(true))
                .setVisible(true);
    }

    private static boolean ensureHeadlessAdminUser(DatabaseManager databaseManager, boolean hasUsers) throws Exception {
        if (hasUsers) {
            return true;
        }

        String adminPassword = System.getenv("BEST_BRIGHTNESS_ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            System.out.println("No users configured. Set BEST_BRIGHTNESS_ADMIN_PASSWORD and rerun.");
            return false;
        }
        databaseManager.createInitialAdmin(adminPassword);
        return true;
    }
}
