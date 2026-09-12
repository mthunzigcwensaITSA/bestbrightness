package com.bestbrightness.pos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private int id;
    private double total;
    private double discount;
    private double finalTotal;
    private LocalDate saleDate;
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {
    }

    public Sale(double total, double discount, double finalTotal, LocalDate saleDate, List<SaleItem> items) {
        this.total = total;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.saleDate = saleDate;
        this.items = new ArrayList<>(items);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public List<SaleItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<SaleItem> items) {
        this.items = new ArrayList<>(items);
    }
}
