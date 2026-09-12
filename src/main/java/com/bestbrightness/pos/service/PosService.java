package com.bestbrightness.pos.service;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.model.Product;
import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PosService {

    private static final double DISCOUNT_THRESHOLD = 500.0;
    private static final double DISCOUNT_RATE = 0.10;

    private final DatabaseManager databaseManager;

    public PosService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Product addProduct(String name, double price, int quantity) throws SQLException {
        validateProduct(name, price, quantity);

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO products (product_name, price, quantity)
                     VALUES (?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name.trim());
            statement.setDouble(2, price);
            statement.setInt(3, quantity);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Product(keys.getInt(1), name.trim(), price, quantity);
                }
            }
        }

        throw new SQLException("Product could not be saved.");
    }

    public List<Product> getProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT product_id, product_name, price, quantity
                     FROM products
                     ORDER BY product_name
                     """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity")));
            }
        }

        return products;
    }

    public SaleItem createSaleItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Select a product.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds stock.");
        }
        return new SaleItem(product.getId(), product.getName(), quantity, product.getPrice() * quantity);
    }

    public double calculateDiscount(double total) {
        return total >= DISCOUNT_THRESHOLD ? total * DISCOUNT_RATE : 0.0;
    }

    public Sale completeSale(List<SaleItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Add at least one item to the cart.");
        }

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<SaleItem> persistedItems = normalizeSaleItems(connection, items);
                double total = persistedItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
                double discount = calculateDiscount(total);
                double finalTotal = total - discount;
                LocalDate saleDate = LocalDate.now();
                int saleId = insertSale(connection, total, discount, finalTotal, saleDate);
                for (SaleItem item : persistedItems) {
                    updateStock(connection, item);
                    insertSaleItem(connection, saleId, item);
                    item.setSaleId(saleId);
                }
                connection.commit();

                Sale sale = new Sale(total, discount, finalTotal, saleDate, persistedItems);
                sale.setId(saleId);
                return sale;
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void validateProduct(String name, double price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    private int insertSale(Connection connection, double total, double discount, double finalTotal, LocalDate saleDate)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO sales (total, discount, final_total, sale_date)
                VALUES (?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, total);
            statement.setDouble(2, discount);
            statement.setDouble(3, finalTotal);
            statement.setDate(4, Date.valueOf(saleDate));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Sale could not be saved.");
    }

    private List<SaleItem> normalizeSaleItems(Connection connection, List<SaleItem> items) throws SQLException {
        List<SaleItem> normalizedItems = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT product_name, price
                FROM products
                WHERE product_id = ?
                """)) {
            for (SaleItem item : items) {
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than zero.");
                }
                statement.setInt(1, item.getProductId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new IllegalArgumentException("Product no longer exists.");
                    }
                    normalizedItems.add(new SaleItem(
                            item.getProductId(),
                            resultSet.getString("product_name"),
                            item.getQuantity(),
                            resultSet.getDouble("price") * item.getQuantity()));
                }
            }
        }
        return normalizedItems;
    }

    private void insertSaleItem(Connection connection, int saleId, SaleItem item) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO sale_items (sale_id, product_id, quantity, subtotal)
                VALUES (?, ?, ?, ?)
                """)) {
            statement.setInt(1, saleId);
            statement.setInt(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            statement.setDouble(4, item.getSubtotal());
            statement.executeUpdate();
        }
    }

    private void updateStock(Connection connection, SaleItem item) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE products
                SET quantity = quantity - ?
                WHERE product_id = ? AND quantity >= ?
                """)) {
            statement.setInt(1, item.getQuantity());
            statement.setInt(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Insufficient stock for " + item.getProductName() + ".");
            }
        }
    }
}
