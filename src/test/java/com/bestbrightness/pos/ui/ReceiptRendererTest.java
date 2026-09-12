package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.Sale;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiptRendererTest {

    @Test
    void returnsWarningFallbackWhenReceiptRenderingFails() {
        Sale sale = new Sale(100.0, 0.0, 100.0, LocalDate.of(2026, 9, 12), List.of());

        ReceiptRenderResult result = ReceiptRenderer.render(sale, ignored -> {
            throw new IllegalStateException("boom");
        });

        assertTrue(result.warning());
        assertEquals("Sale completed, but the receipt could not be generated.", result.text());
    }

    @Test
    void returnsRenderedReceiptWhenGenerationSucceeds() {
        Sale sale = new Sale(100.0, 0.0, 100.0, LocalDate.of(2026, 9, 12), List.of());

        ReceiptRenderResult result = ReceiptRenderer.render(sale, ignored -> "receipt");

        assertFalse(result.warning());
        assertEquals("receipt", result.text());
    }
}
