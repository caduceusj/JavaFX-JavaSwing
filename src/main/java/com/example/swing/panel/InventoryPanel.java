package com.example.swing.panel;

import com.example.swing.model.Product;
import com.example.swing.util.ProductTableModel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class InventoryPanel extends JPanel {

    private static final Color COLOR_LOW   = new Color(255, 220, 220);
    private static final Color COLOR_OK    = new Color(220, 255, 220);
    private static final Color COLOR_EVEN  = new Color(248, 249, 252);
    private static final Color COLOR_ODD   = Color.WHITE;

    private final ProductTableModel tableModel = new ProductTableModel();
    private final JTable table;
    private final SummaryPanel summaryPanel;
    private final List<Product> masterList;
    private int nextId = 100;

    private final Consumer<String> statusCallback;

    public InventoryPanel(Consumer<String> statusCallback) {
        this.statusCallback = statusCallback;
        this.masterList = buildSampleData();

        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Summary cards
        summaryPanel = new SummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);

        // Table
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    Product p = tableModel.getProduct(row);
                    if (p.isLowStock()) {
                        c.setBackground(COLOR_LOW);
                    } else {
                        c.setBackground(row % 2 == 0 ? COLOR_EVEN : COLOR_ODD);
                    }
                }
                return c;
            }
        };
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 215)));

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadData(masterList);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        // Left: buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btns.setOpaque(false);

        JButton addBtn    = createButton("+ Adicionar",  new Color(34, 139, 34));
        JButton editBtn   = createButton("Editar",       new Color(70, 130, 180));
        JButton deleteBtn = createButton("Excluir",      new Color(178, 34, 34));
        JButton exportBtn = createButton("Exportar CSV", new Color(100, 100, 120));

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
        exportBtn.addActionListener(e -> onExport());

        btns.add(addBtn);
        btns.add(editBtn);
        btns.add(deleteBtn);
        btns.add(Box.createHorizontalStrut(16));
        btns.add(exportBtn);

        // Right: search
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        search.setOpaque(false);
        search.add(new JLabel("Buscar:"));
        JTextField searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Nome ou categoria...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() { tableModel.filter(searchField.getText()); }
        });
        search.add(searchField);

        toolbar.add(btns, BorderLayout.WEST);
        toolbar.add(search, BorderLayout.EAST);
        return toolbar;
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 225, 235));
        table.setShowVerticalLines(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(34, 85, 170));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Column widths
        int[] widths = {50, 200, 150, 90, 70, 100, 70};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Center numeric/status columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : new int[]{0, 3, 4, 5, 6}) {
            table.getColumnModel().getColumn(col).setCellRenderer(center);
        }

        // Price formatted
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                if (v instanceof Double d) v = String.format("R$ %.2f", d);
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 0, 0, 8));
                return this;
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(priceRenderer);

        // Status colored
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                if ("BAIXO".equals(v)) {
                    setForeground(new Color(180, 0, 0));
                } else {
                    setForeground(new Color(0, 130, 0));
                }
                return this;
            }
        };
        table.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
    }

    private void loadData(List<Product> products) {
        tableModel.setProducts(products);
        summaryPanel.update(products);
    }

    private void onAdd() {
        ProductFormDialog dlg = new ProductFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), null, nextId++);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            masterList.add(dlg.getResult());
            tableModel.addProduct(dlg.getResult());
            summaryPanel.update(masterList);
            statusCallback.accept("Produto '" + dlg.getResult().getName() + "' adicionado.");
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { showSelectWarning(); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Product existing = tableModel.getProduct(modelRow);

        ProductFormDialog dlg = new ProductFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), existing, existing.getId());
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            Product updated = dlg.getResult();
            updated.setId(existing.getId());
            tableModel.updateProduct(modelRow, updated);
            masterList.set(masterList.indexOf(existing), updated);
            summaryPanel.update(masterList);
            statusCallback.accept("Produto '" + updated.getName() + "' atualizado.");
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { showSelectWarning(); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Product p = tableModel.getProduct(modelRow);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja excluir o produto '" + p.getName() + "'?",
            "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            masterList.remove(p);
            tableModel.removeProduct(modelRow);
            summaryPanel.update(masterList);
            statusCallback.accept("Produto '" + p.getName() + "' excluído.");
        }
    }

    private void onExport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar CSV");
        chooser.setSelectedFile(new File("estoque.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,Nome,Categoria,Preco,Quantidade,Estoque Minimo,Status");
            for (Product p : masterList) {
                pw.printf("%d,%s,%s,%.2f,%d,%d,%s%n",
                    p.getId(), p.getName(), p.getCategory(),
                    p.getPrice(), p.getQuantity(), p.getMinStock(),
                    p.isLowStock() ? "BAIXO" : "OK");
            }
            statusCallback.accept("Exportado para: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Arquivo exportado com sucesso!", "Exportar CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectWarning() {
        JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private List<Product> buildSampleData() {
        return new ArrayList<>(Arrays.asList(
            new Product(1,  "Arroz Branco 5kg",       "Cereais e Grãos",       18.90,  45, 10),
            new Product(2,  "Feijão Preto 1kg",        "Cereais e Grãos",        8.50,   3,  8),
            new Product(3,  "Açúcar Cristal 1kg",      "Cereais e Grãos",        4.75,  30,  5),
            new Product(4,  "Leite Integral 1L",       "Laticínios",             5.49,   2, 20),
            new Product(5,  "Queijo Mussarela 500g",   "Laticínios",            19.90,  12,  5),
            new Product(6,  "Iogurte Natural 170g",    "Laticínios",             3.20,  18,  8),
            new Product(7,  "Frango Inteiro 1kg",      "Carnes e Aves",         12.99,   7, 10),
            new Product(8,  "Carne Moída 500g",        "Carnes e Aves",         18.50,   4,  6),
            new Product(9,  "Banana Prata (kg)",       "Frutas e Verduras",      3.50,  25, 10),
            new Product(10, "Maçã Fuji (kg)",          "Frutas e Verduras",      6.90,  14,  8),
            new Product(11, "Tomate (kg)",              "Frutas e Verduras",      5.20,   1,  5),
            new Product(12, "Água Mineral 1,5L",       "Bebidas",                2.50,  60, 20),
            new Product(13, "Suco de Laranja 1L",      "Bebidas",                8.90,   9, 12),
            new Product(14, "Refrigerante Cola 2L",    "Bebidas",                7.50,  22, 10),
            new Product(15, "Pão de Forma Integral",   "Padaria",                9.90,   5,  6),
            new Product(16, "Detergente 500ml",        "Limpeza",                2.80,  38, 10),
            new Product(17, "Sabão em Pó 1kg",         "Limpeza",               12.50,   2,  5),
            new Product(18, "Shampoo 400ml",           "Higiene Pessoal",       15.90,  20,  8),
            new Product(19, "Sorvete 1,5L",            "Congelados",            16.90,   0,  4),
            new Product(20, "Batata Chips 100g",       "Salgadinhos e Doces",    6.50,  35, 12)
        ));
    }
}
