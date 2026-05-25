package com.example.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane root;
    @FXML private StackPane  contentArea;
    @FXML private Button     btnDashboard;
    @FXML private Button     btnTransactions;

    private javafx.scene.Node dashboardView;
    private javafx.scene.Node transactionView;
    private DashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadViews();
        showDashboard();
    }

    private void loadViews() {
        try {
            FXMLLoader dashLoader = new FXMLLoader(
                getClass().getResource("/com/example/javafx/fxml/dashboard.fxml"));
            dashboardView = dashLoader.load();
            dashboardController = dashLoader.getController();

            FXMLLoader txLoader = new FXMLLoader(
                getClass().getResource("/com/example/javafx/fxml/transactions.fxml"));
            transactionView = txLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        setActiveButton(btnDashboard);
        contentArea.getChildren().setAll(dashboardView);
        if (dashboardController != null) dashboardController.refresh();
    }

    @FXML
    private void showTransactions() {
        setActiveButton(btnTransactions);
        contentArea.getChildren().setAll(transactionView);
    }

    private void setActiveButton(Button active) {
        btnDashboard.getStyleClass().remove("nav-active");
        btnTransactions.getStyleClass().remove("nav-active");
        active.getStyleClass().add("nav-active");
    }
}
