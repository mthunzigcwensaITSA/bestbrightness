package com.bestbrightness.pos.service;

import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiptGeneratorTest {

    @Test
    void generatesReceiptWithItemsAndTotals() {
        Sale sale = new Sale(
                600.0,
                60.0,
                540.0,
                LocalDate.of(2026, 9, 12),
                List.of(
                        new SaleItem(1, "Bleach", 2, 500.0),
                        new SaleItem(2, "Soap", 1, 100.0)));
        sale.setId(7);

        String receipt = ReceiptGenerator.generate(sale);

        assertTrue(receipt.contains("Best Brightness Receipt"));
        assertTrue(receipt.contains("Sale ID: 7"));
        assertTrue(receipt.contains("Date: 2026-09-12"));
        assertTrue(receipt.contains("- Bleach x 2 = R500.00"));
        assertTrue(receipt.contains("- Soap x 1 = R100.00"));
        assertTrue(receipt.contains("Total: R600.00"));
        assertTrue(receipt.contains("Discount: R60.00"));
        assertTrue(receipt.contains("Final Total: R540.00"));
    }
}
