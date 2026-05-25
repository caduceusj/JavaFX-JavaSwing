package com.example.swing.util;

import com.example.swing.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
        "ID", "Nome", "Categoria", "Preço (R$)", "Qtd.", "Estoque Mín.", "Status"
    };

    private List<Product> products = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.allProducts = new ArrayList<>(products);
        this.products = new ArrayList<>(products);
        fireTableDataChanged();
    }

    public void addProduct(Product p) {
        allProducts.add(p);
        products.add(p);
        fireTableRowsInserted(products.size() - 1, products.size() - 1);
    }

    public void updateProduct(int row, Product p) {
        products.set(row, p);
        int allIndex = allProducts.indexOf(getProduct(row));
        if (allIndex >= 0) allProducts.set(allIndex, p);
        fireTableRowsUpdated(row, row);
    }

    public void removeProduct(int row) {
        Product p = products.get(row);
        allProducts.remove(p);
        products.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public Product getProduct(int row) {
        return products.get(row);
    }

    public void filter(String text) {
        if (text == null || text.isBlank()) {
            products = new ArrayList<>(allProducts);
        } else {
            String lower = text.toLowerCase();
            products = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                          || p.getCategory().toLowerCase().contains(lower))
                .collect(java.util.stream.Collectors.toList());
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return products.size(); }

    @Override
    public int getColumnCount() { return COLUMNS.length; }

    @Override
    public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> Integer.class;
            case 3 -> Double.class;
            case 4, 5 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) { return false; }

    @Override
    public Object getValueAt(int row, int col) {
        Product p = products.get(row);
        return switch (col) {
            case 0 -> p.getId();
            case 1 -> p.getName();
            case 2 -> p.getCategory();
            case 3 -> p.getPrice();
            case 4 -> p.getQuantity();
            case 5 -> p.getMinStock();
            case 6 -> p.isLowStock() ? "BAIXO" : "OK";
            default -> "";
        };
    }
}
