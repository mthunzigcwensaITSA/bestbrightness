package com.bestbrightness.pos.service;

import com.bestbrightness.pos.model.Sale;
import com.bestbrightness.pos.model.SaleItem;
import java.time.format.DateTimeFormatter;

public final class ReceiptGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ReceiptGenerator() {
    }

    public static String generate(Sale sale) {
        StringBuilder builder = new StringBuilder();
        builder.append("Best Brightness Receipt\n");
        builder.append("Sale ID: ").append(sale.getId()).append('\n');
        builder.append("Date: ").append(sale.getSaleDate().format(DATE_FORMATTER)).append("\n\n");
        builder.append("Items:\n");
        for (SaleItem item : sale.getItems()) {
            builder.append("- ")
                    .append(item.getProductName())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" = R")
                    .append(String.format("%.2f", item.getSubtotal()))
                    .append('\n');
        }
        builder.append("\nTotal: R").append(String.format("%.2f", sale.getTotal())).append('\n');
        builder.append("Discount: R").append(String.format("%.2f", sale.getDiscount())).append('\n');
        builder.append("Final Total: R").append(String.format("%.2f", sale.getFinalTotal())).append('\n');
        return builder.toString();
    }
}
