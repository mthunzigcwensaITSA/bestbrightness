package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.Product;
import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import com.bestbrightness.pos.model.User;
import com.bestbrightness.pos.service.PosService;
import com.bestbrightness.pos.service.ReceiptGenerator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    private final JLabel totalLabel = new JLabel("Total: R0.00");
    private final JLabel discountLabel = new JLabel("Discount: R0.00");
    private final JLabel finalTotalLabel = new JLabel("Final Total: R0.00");
    private final JTextArea receiptArea = new JTextArea(12, 32);

    public MainFrame(User user, PosService posService) {
        this.posService = posService;
        initialize(user);
        loadProducts();
    }

    private void initialize(User user) {
        setTitle("Best Brightness POS - Welcome " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Products", createProductsPanel());
        tabbedPane.addTab("Sales", createSalesPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(productNameField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(productPriceField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(productQuantityField);

        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(event -> addProduct());
        formPanel.add(new JLabel());
        formPanel.add(addButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(productTableModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        topPanel.add(new JLabel("Product:"));
        topPanel.add(productComboBox);
        topPanel.add(new JLabel("Quantity:"));
        topPanel.add(saleQuantityField);

        JButton addToCartButton = new JButton("Add To Cart");
        addToCartButton.addActionListener(event -> addToCart());
        JButton completeSaleButton = new JButton("Complete Sale");
        completeSaleButton.addActionListener(event -> completeSale());
        topPanel.add(addToCartButton);
        topPanel.add(completeSaleButton);

        receiptArea.setEditable(false);

        JPanel totalsPanel = new JPanel(new GridLayout(3, 1));
        totalsPanel.add(totalLabel);
        totalsPanel.add(discountLabel);
        totalsPanel.add(finalTotalLabel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(totalsPanel, BorderLayout.NORTH);
        southPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(cartTableModel)), BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
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
            int cartQuantity = getCartQuantityForProduct(product == null ? 0 : product.getId());
            if (product != null && cartQuantity + quantity > product.getQuantity()) {
                throw new IllegalArgumentException("Requested quantity exceeds stock.");
            }
            SaleItem item = posService.createSaleItem(product, quantity);
            cartItems.add(item);
            cartTableModel.addRow(new Object[]{
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("R%.2f", item.getSubtotal())
            });
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
            receiptArea.setText(ReceiptGenerator.generate(sale));
            JOptionPane.showMessageDialog(this, "Sale completed successfully.");
        } catch (IllegalArgumentException | SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Sale Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException exception) {
            receiptArea.setText("Sale completed, but the receipt could not be generated.");
            JOptionPane.showMessageDialog(this, receiptArea.getText(), "Receipt Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = posService.getProducts();
            productTableModel.setRowCount(0);
            DefaultComboBoxModel<Product> comboBoxModel = new DefaultComboBoxModel<>();
            for (Product product : products) {
                productTableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        String.format("R%.2f", product.getPrice()),
                        product.getQuantity()
                });
                if (product.getQuantity() > 0) {
                    comboBoxModel.addElement(product);
                }
            }
            productComboBox.setModel(comboBoxModel);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotals() {
        double total = cartItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
        double discount = posService.calculateDiscount(total);
        totalLabel.setText("Total: R" + String.format("%.2f", total));
        discountLabel.setText("Discount: R" + String.format("%.2f", discount));
        finalTotalLabel.setText("Final Total: R" + String.format("%.2f", total - discount));
    }

    private int getCartQuantityForProduct(int productId) {
        return cartItems.stream()
                .filter(item -> item.getProductId() == productId)
                .mapToInt(SaleItem::getQuantity)
                .sum();
    }
}
