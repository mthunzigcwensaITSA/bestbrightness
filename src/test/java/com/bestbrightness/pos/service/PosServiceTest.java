package com.bestbrightness.pos.service;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.model.Product;
import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PosServiceTest {

    @TempDir
    Path tempDir;

    private PosService posService;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager databaseManager = new DatabaseManager(tempDir.resolve("test.db"));
        databaseManager.initializeDatabase();
        posService = new PosService(databaseManager);
    }

    @Test
    void appliesDiscountAndUpdatesStockOnCompletedSale() throws Exception {
        Product bleach = posService.addProduct("Bleach", 250.0, 5);
        Product soap = posService.addProduct("Soap", 100.0, 10);

        List<SaleItem> cartItems = List.of(
                posService.createSaleItem(bleach, 2),
                posService.createSaleItem(soap, 1));

        Sale sale = posService.completeSale(cartItems);

        assertNotNull(sale);
        assertNotNull(sale.getSaleDate());
        assertEquals(600.0, sale.getTotal(), 0.001);
        assertEquals(60.0, sale.getDiscount(), 0.001);
        assertEquals(540.0, sale.getFinalTotal(), 0.001);

        List<Product> products = posService.getProducts();
        Product updatedBleach = products.stream().filter(product -> product.getId() == bleach.getId()).findFirst().orElseThrow();
        Product updatedSoap = products.stream().filter(product -> product.getId() == soap.getId()).findFirst().orElseThrow();

        assertEquals(3, updatedBleach.getQuantity());
        assertEquals(9, updatedSoap.getQuantity());
    }

    @Test
    void keepsDiscountAtZeroBelowThreshold() {
        assertEquals(0.0, posService.calculateDiscount(499.99), 0.001);
    }
}
