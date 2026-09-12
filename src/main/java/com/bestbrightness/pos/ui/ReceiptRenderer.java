package com.bestbrightness.pos.ui;

import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.service.ReceiptGenerator;
import java.util.function.Function;

public final class ReceiptRenderer {

    private static final String FALLBACK_TEXT = "Sale completed, but the receipt could not be generated.";

    private ReceiptRenderer() {
    }

    public static ReceiptRenderResult render(Sale sale) {
        return render(sale, ReceiptGenerator::generate);
    }

    static ReceiptRenderResult render(Sale sale, Function<Sale, String> generator) {
        try {
            return new ReceiptRenderResult(generator.apply(sale), false);
        } catch (RuntimeException exception) {
            return new ReceiptRenderResult(FALLBACK_TEXT, true);
        }
    }
}
