package com.example.javafx.controller;

import com.example.javafx.model.Transaction;
import com.example.javafx.service.DataService;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML private Label saldoLabel;
    @FXML private Label receitaLabel;
    @FXML private Label despesaLabel;
    @FXML private Label economiasLabel;
    @FXML private Label saldoTrend;
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis barCategoryAxis;
    @FXML private NumberAxis barNumberAxis;
    @FXML private VBox recentList;

    private final DataService dataService = DataService.get();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refresh();
    }

    public void refresh() {
        updateSummaryCards();
        updatePieChart();
        updateBarChart();
        updateRecentList();
    }

    private void updateSummaryCards() {
        double receita  = dataService.getTotalReceita();
        double despesa  = dataService.getTotalDespesa();
        double saldo    = dataService.getSaldo();
        double economia = receita > 0 ? ((receita - despesa) / receita * 100) : 0;

        animateLabel(saldoLabel,   saldo);
        animateLabel(receitaLabel, receita);
        animateLabel(despesaLabel, despesa);
        economiasLabel.setText(String.format("%.1f%%", Math.max(0, economia)));

        if (saldo >= 0) {
            saldoLabel.getStyleClass().removeAll("negative");
            saldoTrend.setText("Saldo positivo");
            saldoTrend.setStyle("-fx-text-fill: #4CAF50;");
        } else {
            if (!saldoLabel.getStyleClass().contains("negative")) {
                saldoLabel.getStyleClass().add("negative");
            }
            saldoTrend.setText("Saldo negativo");
            saldoTrend.setStyle("-fx-text-fill: #F44336;");
        }
    }

    private void animateLabel(Label label, double targetValue) {
        final long[] start = {System.currentTimeMillis()};
        final double duration = 800;
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsed = (System.currentTimeMillis() - start[0]);
                double progress = Math.min(1.0, elapsed / duration);
                double current = targetValue * easeOut(progress);
                label.setText(String.format("R$ %,.2f", current));
                if (progress >= 1.0) stop();
            }
        };
        timer.start();
    }

    private double easeOut(double t) { return 1 - Math.pow(1 - t, 3); }

    private void updatePieChart() {
        Map<String, Double> data = dataService.getDespesasByCategory();
        ObservableList<PieChart.Data> pieData = javafx.collections.FXCollections.observableArrayList();

        data.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> pieData.add(new PieChart.Data(e.getKey(), e.getValue())));

        pieChart.setData(pieData);
        pieChart.setTitle("Despesas por Categoria");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);

        // Fade-in animation
        pieChart.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), pieChart);
        ft.setToValue(1.0);
        ft.play();
    }

    private void updateBarChart() {
        Map<String, Double> monthlyBalance = dataService.getBalanceByMonth();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Saldo Mensal");

        monthlyBalance.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));

        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setTitle("Evolução do Saldo por Mês");
        barChart.setAnimated(true);
    }

    private void updateRecentList() {
        recentList.getChildren().clear();

        dataService.getTransactions().stream()
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .limit(5)
            .forEach(t -> recentList.getChildren().add(buildRecentItem(t)));
    }

    private javafx.scene.layout.HBox buildRecentItem(Transaction t) {
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(12);
        row.getStyleClass().add("recent-item");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label icon = new Label(t.isReceita() ? "+" : "-");
        icon.getStyleClass().add(t.isReceita() ? "badge-income" : "badge-expense");

        VBox info = new VBox(2);
        Label desc = new Label(t.getDescription());
        desc.getStyleClass().add("recent-desc");
        Label cat = new Label(t.getCategory() + "  •  " + t.getDateStr());
        cat.getStyleClass().add("recent-cat");
        info.getChildren().addAll(desc, cat);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label amount = new Label(String.format("R$ %,.2f", t.getAmount()));
        amount.getStyleClass().add(t.isReceita() ? "amount-income" : "amount-expense");

        row.getChildren().addAll(icon, info, spacer, amount);
        return row;
    }
}
