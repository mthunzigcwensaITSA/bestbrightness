package com.bestbrightness.pos.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public final class UiTheme {

    public static final Color BACKGROUND = new Color(15, 23, 42);
    public static final Color SURFACE = new Color(30, 41, 59);
    public static final Color SURFACE_ALT = new Color(51, 65, 85);
    public static final Color ACCENT = new Color(56, 189, 248);
    public static final Color ACCENT_ALT = new Color(139, 92, 246);
    public static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color WARNING = new Color(251, 191, 36);

    private UiTheme() {
    }

    public static void setup() {
        FlatDarkLaf.setup();
        UIManager.put("Component.arc", 18);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("Button.arc", 18);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("TabbedPane.arc", 18);
        UIManager.put("TabbedPane.selectedBackground", SURFACE);
        UIManager.put("TabbedPane.underlineColor", ACCENT);
        UIManager.put("TabbedPane.focusColor", ACCENT);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", SURFACE_ALT);
        UIManager.put("Table.selectionBackground", ACCENT_ALT);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 28f));
        return label;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 17f));
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JLabel createMetricValue(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        return label;
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT);
        button.setForeground(new Color(15, 23, 42));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setMargin(new Insets(10, 18, 10, 18));
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(SURFACE_ALT);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setMargin(new Insets(10, 18, 10, 18));
        return button;
    }

    public static void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(220, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setBackground(new Color(18, 25, 41));
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setCaretColor(TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(18, 25, 41));
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT_ALT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(SURFACE_ALT);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
    }

    public static JPanel createMetricCard(String labelText, JLabel valueLabel, Color accentColor) {
        JPanel card = createCard();
        card.setLayout(new java.awt.BorderLayout(0, 8));
        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(0, 5));
        card.add(accent, java.awt.BorderLayout.NORTH);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        card.add(valueLabel, java.awt.BorderLayout.CENTER);
        card.add(createMutedLabel(labelText), java.awt.BorderLayout.SOUTH);
        return card;
    }

    public static JTextArea createInfoText(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setForeground(TEXT_SECONDARY);
        textArea.setBorder(BorderFactory.createEmptyBorder());
        return textArea;
    }

    public static Dimension fitToScreen(int preferredWidth, int preferredHeight) {
        if (GraphicsEnvironment.isHeadless()) {
            return new Dimension(preferredWidth, preferredHeight);
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.max(360, Math.min(preferredWidth, screenSize.width - 80));
        int height = Math.max(360, Math.min(preferredHeight, screenSize.height - 80));
        return new Dimension(width, height);
    }

    public static void setContentPadding(JComponent component) {
        component.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    public static void applyForegroundRecursively(Component component) {
        if (component instanceof JLabel label) {
            label.setForeground(TEXT_PRIMARY);
        }
        if (component instanceof JPanel panel && panel.getBackground() == null) {
            panel.setBackground(BACKGROUND);
        }
        if (component instanceof JComponent jComponent && !(jComponent instanceof JTable)) {
            jComponent.setOpaque(true);
        }
        if (component instanceof java.awt.Container container) {
            for (Component child : container.getComponents()) {
                applyForegroundRecursively(child);
            }
        }
    }
}
