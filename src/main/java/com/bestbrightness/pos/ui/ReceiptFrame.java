package com.bestbrightness.pos.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReceiptFrame extends JFrame {

    private final JTextArea receiptArea = new JTextArea();

    public ReceiptFrame() {
        initialize();
    }

    private void initialize() {
        setTitle("Best Brightness POS - Receipt Slip");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(360, 440));

        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(UiTheme.createSectionLabel("Receipt slip"));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(UiTheme.createInfoText(
                "Every completed sale opens here so the cashier can immediately review the generated slip."));

        UiTheme.styleTextArea(receiptArea);
        receiptArea.setEditable(false);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(card, BorderLayout.CENTER);

        setContentPane(root);
        setSize(UiTheme.fitToScreen(460, 560));
    }

    public void showReceipt(Component parent, ReceiptRenderResult receipt) {
        setTitle(receipt.warning()
                ? "Best Brightness POS - Receipt Warning"
                : "Best Brightness POS - Receipt Slip");
        receiptArea.setText(receipt.text());
        receiptArea.setCaretPosition(0);
        setLocationRelativeTo(parent);
        setVisible(true);
        toFront();
    }
}
