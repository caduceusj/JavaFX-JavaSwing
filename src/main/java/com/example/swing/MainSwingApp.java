package com.example.swing;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Java Swing Supermarket Inventory application.
 *
 * Run via Maven:
 *   mvn exec:java -Dexec.mainClass=com.example.swing.MainSwingApp
 *
 * Or in Eclipse: Run As > Java Application (select this class).
 */
public class MainSwingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
