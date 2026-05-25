package com.example.swing;

import com.example.swing.panel.InventoryPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JLabel statusLabel;

    public MainFrame() {
        super("SuperMarket Manager — Controle de Estoque");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        setupLookAndFeel();
        setIconImage(createAppIcon());
        buildUI();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", new Color(34, 85, 170));
            UIManager.put("nimbusBlueGrey", new Color(180, 195, 220));
            UIManager.put("control", new Color(245, 247, 250));
        } catch (Exception ignored) {}
    }

    private Image createAppIcon() {
        // Simple programmatic icon (32x32 blue square with "S")
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(34, 85, 170));
        g.fillRoundRect(0, 0, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("S", 9, 24);
        g.dispose();
        return img;
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 65, 140));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("SuperMarket Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Controle de Estoque em tempo real");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(180, 200, 240));

        JPanel titlePanel = new JPanel(new BorderLayout(2, 2));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        header.add(titlePanel, BorderLayout.WEST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("  Inventario  ", new InventoryPanel(this::setStatus));

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        statusBar.setBackground(new Color(220, 225, 235));
        statusBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(190, 195, 210)));
        statusLabel = new JLabel("Pronto.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(80, 90, 110));
        statusBar.add(statusLabel);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg);
    }
}
