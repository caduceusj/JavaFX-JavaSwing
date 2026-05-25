package com.example.swing.panel;

import com.example.swing.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductFormDialog extends JDialog {

    private static final String[] CATEGORIES = {
        "Frutas e Verduras", "Laticínios", "Carnes e Aves", "Bebidas",
        "Padaria", "Limpeza", "Higiene Pessoal", "Congelados",
        "Cereais e Grãos", "Salgadinhos e Doces"
    };

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JFormattedTextField priceField;
    private JSpinner quantitySpinner;
    private JSpinner minStockSpinner;

    private Product result;
    private final int nextId;

    public ProductFormDialog(Frame parent, Product existing, int nextId) {
        super(parent, existing == null ? "Novo Produto" : "Editar Produto", true);
        this.nextId = nextId;

        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        content.add(buildTitleLabel(existing == null ? "Novo Produto" : "Editar: " + existing.getName()), BorderLayout.NORTH);
        content.add(buildForm(existing), BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(content);
    }

    private JLabel buildTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(34, 85, 170));
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lbl;
    }

    private JPanel buildForm(Product existing) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(label("Nome do produto:"), gbc);
        nameField = new JTextField(existing != null ? existing.getName() : "", 18);
        styleField(nameField);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(nameField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(label("Categoria:"), gbc);
        categoryCombo = new JComboBox<>(CATEGORIES);
        if (existing != null) categoryCombo.setSelectedItem(existing.getCategory());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(categoryCombo, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(label("Preço (R$):"), gbc);
        priceField = new JFormattedTextField();
        priceField.setValue(existing != null ? existing.getPrice() : 0.0);
        styleField(priceField);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(priceField, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(label("Quantidade em estoque:"), gbc);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getQuantity() : 0, 0, 99999, 1));
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(quantitySpinner, gbc);

        // Min stock
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(label("Estoque mínimo:"), gbc);
        minStockSpinner = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getMinStock() : 5, 0, 99999, 1));
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(minStockSpinner, gbc);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setBackground(Color.WHITE);

        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener(e -> dispose());

        JButton save = new JButton("Salvar");
        save.setBackground(new Color(34, 139, 34));
        save.setForeground(Color.WHITE);
        save.setFont(save.getFont().deriveFont(Font.BOLD));
        save.addActionListener(e -> onSave());

        panel.add(cancel);
        panel.add(save);
        return panel;
    }

    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do produto é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().replace(",", ".").replaceAll("[^0-9.]", ""));
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Product(
            nextId,
            name,
            (String) categoryCombo.getSelectedItem(),
            price,
            (Integer) quantitySpinner.getValue(),
            (Integer) minStockSpinner.getValue()
        );
        dispose();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200)),
            new EmptyBorder(4, 6, 4, 6)
        ));
    }

    public Product getResult() { return result; }
}
