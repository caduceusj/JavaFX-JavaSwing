package com.example.swing.model;

public class Product {

    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int minStock;

    public Product(int id, String name, String category, double price, int quantity, int minStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minStock = minStock;
    }

    public boolean isLowStock() {
        return quantity <= minStock;
    }

    public double getTotalValue() {
        return price * quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getMinStock() { return minStock; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setMinStock(int minStock) { this.minStock = minStock; }

    @Override
    public String toString() {
        return name + " [" + category + "] - R$ " + String.format("%.2f", price);
    }
}
