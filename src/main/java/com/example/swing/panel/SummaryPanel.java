package com.example.swing.panel;

import com.example.swing.model.Product;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class SummaryPanel extends JPanel {

    private JLabel totalProductsLabel;
    private JLabel lowStockLabel;
    private JLabel totalValueLabel;
    private JLabel categoriesLabel;

    public SummaryPanel() {
        setLayout(new GridLayout(1, 4, 12, 0));
        setBorder(new EmptyBorder(0, 0, 12, 0));
        setBackground(new Color(245, 247, 250));

        totalProductsLabel = addCard("Total de Produtos", "0", new Color(63, 114, 175));
        lowStockLabel      = addCard("Estoque Baixo",     "0", new Color(200, 70, 70));
        totalValueLabel    = addCard("Valor em Estoque",  "R$ 0,00", new Color(46, 139, 87));
        categoriesLabel    = addCard("Categorias",         "0", new Color(148, 103, 189));
    }

    private JLabel addCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accent, 2, true),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(new Color(120, 120, 130));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);

        add(card);
        return valueLbl;
    }

    public void update(List<Product> products) {
        long lowStock = products.stream().filter(Product::isLowStock).count();
        double totalValue = products.stream().mapToDouble(Product::getTotalValue).sum();
        long categories = products.stream().map(Product::getCategory).distinct().count();

        totalProductsLabel.setText(String.valueOf(products.size()));
        lowStockLabel.setText(String.valueOf(lowStock));
        totalValueLabel.setText(String.format("R$ %,.2f", totalValue));
        categoriesLabel.setText(String.valueOf(categories));
    }
}
