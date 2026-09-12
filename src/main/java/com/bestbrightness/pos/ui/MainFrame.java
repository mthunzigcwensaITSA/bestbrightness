package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.Product;
import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import com.bestbrightness.pos.model.User;
import com.bestbrightness.pos.service.PosService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private final PosService posService;
    private final List<SaleItem> cartItems = new ArrayList<>();

    private final DefaultTableModel productTableModel =
            new DefaultTableModel(new Object[]{"ID", "Product", "Price", "Quantity"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final DefaultTableModel cartTableModel =
            new DefaultTableModel(new Object[]{"Product", "Quantity", "Subtotal"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTextField productNameField = new JTextField();
    private final JTextField productPriceField = new JTextField();
    private final JTextField productQuantityField = new JTextField();
    private final JComboBox<Product> productComboBox = new JComboBox<>();
    private final JTextField saleQuantityField = new JTextField();
    private final JLabel totalLabel = UiTheme.createMetricValue("R0.00");
    private final JLabel discountLabel = UiTheme.createMetricValue("R0.00");
    private final JLabel finalTotalLabel = UiTheme.createMetricValue("R0.00");
    private final JLabel inventoryCountLabel = UiTheme.createMetricValue("0");
    private final JLabel inventoryValueLabel = UiTheme.createMetricValue("R0.00");
    private final JLabel lowStockLabel = UiTheme.createMetricValue("0");
    private final JTextArea receiptArea = new JTextArea(12, 32);
    private final ReceiptFrame receiptFrame = new ReceiptFrame();
    private ReceiptRenderResult latestReceipt;

    public MainFrame(User user, PosService posService) {
        this.posService = posService;
        initialize(user);
        loadProducts();
    }

    private void initialize(User user) {
        setTitle("Best Brightness POS - Welcome " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(createHeader(user), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);
        setSize(UiTheme.fitToScreen(1220, 860));
        setLocationRelativeTo(null);
    }

    private JPanel createHeader(User user) {
        JPanel panel = UiTheme.createCard();
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBackground(UiTheme.SURFACE_ALT);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel eyebrow = UiTheme.createMutedLabel("BEST BRIGHTNESS CONTROL CENTER");
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 12f));
        JLabel title = UiTheme.createTitleLabel("Point of Sale Dashboard");
        JTextArea subtitle = UiTheme.createInfoText(
                "Premium cashier experience for products, discounts, receipts, and live stock.");
        left.add(eyebrow);
        left.add(Box.createVerticalStrut(8));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(createChip("Active User", user.getUsername(), UiTheme.ACCENT));
        right.add(Box.createVerticalStrut(10));
        right.add(createChip("Discount Rule", "10% from R500", UiTheme.ACCENT_ALT));

        panel.add(left, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel createChip(String title, String value, java.awt.Color accent) {
        JPanel chip = new JPanel();
        chip.setOpaque(true);
        chip.setBackground(UiTheme.SURFACE);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        JLabel titleLabel = UiTheme.createMutedLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel valueLabel = UiTheme.createSectionLabel(value);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.add(titleLabel);
        chip.add(Box.createVerticalStrut(3));
        chip.add(valueLabel);
        return chip;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Inventory Studio", createScrollableTab(createProductsPanel()));
        tabbedPane.addTab("Sales Command", createScrollableTab(createSalesPanel()));
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createScrollableTab(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createMetricStrip() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 12));
        panel.setOpaque(false);
        panel.add(UiTheme.createMetricCard("Products in catalog", inventoryCountLabel, UiTheme.ACCENT));
        panel.add(UiTheme.createMetricCard("Inventory value", inventoryValueLabel, UiTheme.SUCCESS));
        panel.add(UiTheme.createMetricCard("Low stock items (≤ 5)", lowStockLabel, UiTheme.WARNING));
        return panel;
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createMetricStrip());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createProductFormCard());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createSectionDivider());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createProductTableCard());
        return panel;
    }

    private JPanel createProductFormCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));

        JLabel title = UiTheme.createSectionLabel("Add inventory");
        JTextArea subtitle = UiTheme.createInfoText("Capture premium products with clean pricing and stock levels.");

        UiTheme.styleField(productNameField);
        UiTheme.styleField(productPriceField);
        UiTheme.styleField(productQuantityField);
        productNameField.putClientProperty("JTextField.placeholderText", "Surface cleaner");
        productPriceField.putClientProperty("JTextField.placeholderText", "199.99");
        productQuantityField.putClientProperty("JTextField.placeholderText", "25");

        JButton addButton = UiTheme.createPrimaryButton("Save Product");
        addButton.addActionListener(event -> addProduct());

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(createFieldBlock("Product name", productNameField));
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldBlock("Unit price (R)", productPriceField));
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldBlock("Stock quantity", productQuantityField));
        card.add(Box.createVerticalStrut(18));
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(addButton);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createFieldBlock(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = UiTheme.createMutedLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        return panel;
    }

    private JPanel createProductTableCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = UiTheme.createSectionLabel("Inventory overview");
        JTextArea subtitle = UiTheme.createInfoText("Review available products, pricing, and current stock levels.");
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JTable table = new JTable(productTableModel);
        UiTheme.styleTable(table);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createSalesComposerCard());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createSectionDivider());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createCartCard());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createSectionDivider());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createTotalsCard());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createSectionDivider());
        panel.add(Box.createVerticalStrut(18));
        panel.add(createReceiptCard());
        return panel;
    }

    private JPanel createSalesComposerCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(UiTheme.createSectionLabel("Compose sale"));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(UiTheme.createInfoText("Build the cart with product selection and quantity validation."));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 8, 14);

        form.add(UiTheme.createMutedLabel("Product"), constraints);
        constraints.gridx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 8, 0);
        form.add(UiTheme.createMutedLabel("Quantity"), constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.insets = new java.awt.Insets(0, 0, 0, 14);
        productComboBox.setPreferredSize(new Dimension(320, 42));
        form.add(productComboBox, constraints);

        constraints.gridx = 1;
        constraints.insets = new java.awt.Insets(0, 0, 0, 0);
        UiTheme.styleField(saleQuantityField);
        saleQuantityField.putClientProperty("JTextField.placeholderText", "3");
        form.add(saleQuantityField, constraints);

        JButton addToCartButton = UiTheme.createSecondaryButton("Add to Cart");
        addToCartButton.addActionListener(event -> addToCart());
        JButton completeSaleButton = UiTheme.createPrimaryButton("Complete Sale");
        completeSaleButton.addActionListener(event -> completeSale());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(addToCartButton);
        buttonRow.add(completeSaleButton);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createCartCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(UiTheme.createSectionLabel("Cart overview"));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(UiTheme.createInfoText("Track the selected items before the final checkout action."));

        JTable table = new JTable(cartTableModel);
        UiTheme.styleTable(table);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel createTotalsCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(UiTheme.createSectionLabel("Checkout totals"));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(UiTheme.createInfoText("Instant discount visibility and final pricing summary."));

        JPanel metrics = new JPanel(new GridLayout(3, 1, 0, 12));
        metrics.setOpaque(false);
        metrics.add(createInlineMetric("Cart total", totalLabel));
        metrics.add(createInlineMetric("Discount", discountLabel));
        metrics.add(createInlineMetric("Final total", finalTotalLabel));

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(metrics, BorderLayout.CENTER);
        return card;
    }

    private JPanel createInlineMetric(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(18, 25, 41));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JLabel label = UiTheme.createMutedLabel(labelText);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createReceiptCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(UiTheme.createSectionLabel("Digital receipt"));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(UiTheme.createInfoText("The latest transaction receipt appears here after checkout."));

        UiTheme.styleTextArea(receiptArea);
        receiptArea.setEditable(false);
        JButton openWindowButton = UiTheme.createSecondaryButton("Open Receipt Window");
        openWindowButton.addActionListener(event -> openReceiptWindow());
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        card.add(openWindowButton, BorderLayout.SOUTH);
        return card;
    }

    private JSeparator createSectionDivider() {
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }

    private void addProduct() {
        try {
            Product product = posService.addProduct(
                    productNameField.getText(),
                    Double.parseDouble(productPriceField.getText().trim()),
                    Integer.parseInt(productQuantityField.getText().trim()));
            productNameField.setText("");
            productPriceField.setText("");
            productQuantityField.setText("");
            JOptionPane.showMessageDialog(this, "Product added: " + product.getName());
            loadProducts();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric values for price and quantity.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToCart() {
        try {
            Product product = (Product) productComboBox.getSelectedItem();
            int quantity = Integer.parseInt(saleQuantityField.getText().trim());
            int existingRow = findCartRowByProduct(product == null ? 0 : product.getId());
            int currentQuantity = existingRow >= 0 ? cartItems.get(existingRow).getQuantity() : 0;
            if (product != null && currentQuantity + quantity > product.getQuantity()) {
                throw new IllegalArgumentException("Requested quantity exceeds stock.");
            }
            SaleItem item = posService.createSaleItem(product, currentQuantity + quantity);
            if (existingRow >= 0) {
                cartItems.set(existingRow, item);
                cartTableModel.setValueAt(item.getProductName(), existingRow, 0);
                cartTableModel.setValueAt(item.getQuantity(), existingRow, 1);
                cartTableModel.setValueAt(String.format("R%.2f", item.getSubtotal()), existingRow, 2);
            } else {
                cartItems.add(item);
                cartTableModel.addRow(new Object[]{
                        item.getProductName(),
                        item.getQuantity(),
                        String.format("R%.2f", item.getSubtotal())
                });
            }
            saleQuantityField.setText("");
            updateTotals();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeSale() {
        try {
            Sale sale = posService.completeSale(cartItems);
            cartItems.clear();
            cartTableModel.setRowCount(0);
            updateTotals();
            loadProducts();
            ReceiptRenderResult receipt = ReceiptRenderer.render(sale);
            latestReceipt = receipt;
            receiptArea.setText(receipt.text());
            receiptArea.setCaretPosition(0);
            if (receipt.warning()) {
                JOptionPane.showMessageDialog(this,
                        "Sale completed successfully, but the generated slip used the fallback receipt text.",
                        "Receipt Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sale completed successfully. The receipt slip opened in a new window.");
            }
            receiptFrame.showReceipt(this, receipt);
        } catch (IllegalArgumentException | SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Sale Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = posService.getProducts();
            productTableModel.setRowCount(0);
            DefaultComboBoxModel<Product> comboBoxModel = new DefaultComboBoxModel<>();
            double inventoryValue = 0.0;
            int lowStockCount = 0;
            for (Product product : products) {
                productTableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        String.format("R%.2f", product.getPrice()),
                        product.getQuantity()
                });
                inventoryValue += product.getPrice() * product.getQuantity();
                if (product.getQuantity() <= 5) {
                    lowStockCount++;
                }
                if (product.getQuantity() > 0) {
                    comboBoxModel.addElement(product);
                }
            }
            productComboBox.setModel(comboBoxModel);
            inventoryCountLabel.setText(String.valueOf(products.size()));
            inventoryValueLabel.setText(String.format("R%.2f", inventoryValue));
            lowStockLabel.setText(String.valueOf(lowStockCount));
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotals() {
        double total = cartItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
        double discount = posService.calculateDiscount(total);
        totalLabel.setText(String.format("R%.2f", total));
        discountLabel.setText(String.format("R%.2f", discount));
        finalTotalLabel.setText(String.format("R%.2f", total - discount));
    }

    private int findCartRowByProduct(int productId) {
        for (int index = 0; index < cartItems.size(); index++) {
            if (cartItems.get(index).getProductId() == productId) {
                return index;
            }
        }
        return -1;
    }

    private void openReceiptWindow() {
        if (latestReceipt == null) {
            JOptionPane.showMessageDialog(this, "Complete a sale first to generate a receipt slip.",
                    "Receipt Unavailable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        receiptFrame.showReceipt(this, latestReceipt);
    }
}
