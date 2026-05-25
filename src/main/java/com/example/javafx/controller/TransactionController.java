package com.example.javafx.controller;

import com.example.javafx.model.Transaction;
import com.example.javafx.model.Transaction.Type;
import com.example.javafx.service.DataService;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class TransactionController implements Initializable {

    @FXML private TableView<Transaction> tableView;
    @FXML private TableColumn<Transaction, Integer>   colId;
    @FXML private TableColumn<Transaction, String>    colDesc;
    @FXML private TableColumn<Transaction, String>    colCategory;
    @FXML private TableColumn<Transaction, Double>    colAmount;
    @FXML private TableColumn<Transaction, Type>      colType;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, Void>      colActions;

    @FXML private TextField        searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterCategory;

    @FXML private TextField   descField;
    @FXML private TextField   amountField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker  datePicker;
    @FXML private Label       formFeedback;

    private final DataService dataService = DataService.get();
    private FilteredList<Transaction> filtered;

    private static final String[] CATEGORIES = {
        "Salário", "Renda Extra", "Alimentação", "Moradia",
        "Transporte", "Saúde", "Lazer", "Educação",
        "Vestuário", "Investimentos", "Outros"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFilters();
        setupForm();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(String.format("R$ %,.2f", v));
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Type v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(v == Type.RECEITA ? "Receita" : "Despesa");
                badge.getStyleClass().add(v == Type.RECEITA ? "badge-income" : "badge-expense");
                setGraphic(badge);
                setText(null);
            }
        });

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        });

        addActionsColumn();

        filtered = new FilteredList<>(dataService.getTransactions(), t -> true);
        tableView.setItems(filtered);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPlaceholder(new Label("Nenhuma transação encontrada."));
    }

    private void addActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Excluir");
            {
                deleteBtn.getStyleClass().add("btn-danger-sm");
                deleteBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Excluir \"" + t.getDescription() + "\"?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirmar");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) dataService.removeTransaction(t);
                    });
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void setupFilters() {
        filterType.getItems().addAll("Todos", "Receita", "Despesa");
        filterType.setValue("Todos");

        filterCategory.getItems().add("Todas");
        filterCategory.getItems().addAll(CATEGORIES);
        filterCategory.setValue("Todas");

        searchField.textProperty().addListener((o, old, nw) -> applyFilter());
        filterType.valueProperty().addListener((o, old, nw) -> applyFilter());
        filterCategory.valueProperty().addListener((o, old, nw) -> applyFilter());
    }

    private void applyFilter() {
        String search = searchField.getText().toLowerCase();
        String type   = filterType.getValue();
        String cat    = filterCategory.getValue();

        filtered.setPredicate(t -> {
            boolean matchSearch = search.isEmpty() || t.getDescription().toLowerCase().contains(search);
            boolean matchType   = "Todos".equals(type)
                || (type.equals("Receita") && t.isReceita())
                || (type.equals("Despesa") && !t.isReceita());
            boolean matchCat    = "Todas".equals(cat) || t.getCategory().equals(cat);
            return matchSearch && matchType && matchCat;
        });
    }

    private void setupForm() {
        categoryCombo.getItems().addAll(CATEGORIES);
        categoryCombo.setValue(CATEGORIES[0]);
        typeCombo.getItems().addAll("Receita", "Despesa");
        typeCombo.setValue("Despesa");
        datePicker.setValue(LocalDate.now());
        formFeedback.setVisible(false);
    }

    @FXML
    private void onAddTransaction() {
        String desc = descField.getText().trim();
        if (desc.isEmpty()) { showFeedback("Informe uma descrição.", false); return; }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().replace(",", "."));
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showFeedback("Valor inválido. Use números positivos.", false);
            return;
        }

        if (datePicker.getValue() == null) { showFeedback("Selecione uma data.", false); return; }

        Type type = "Receita".equals(typeCombo.getValue()) ? Type.RECEITA : Type.DESPESA;
        dataService.addTransaction(new Transaction(0, desc, categoryCombo.getValue(), amount, type, datePicker.getValue()));

        clearForm();
        showFeedback("Transação adicionada com sucesso!", true);
    }

    private void showFeedback(String msg, boolean ok) {
        formFeedback.setText(msg);
        formFeedback.setStyle(ok ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #F44336;");
        formFeedback.setVisible(true);
    }

    private void clearForm() {
        descField.clear();
        amountField.clear();
        categoryCombo.setValue(CATEGORIES[0]);
        typeCombo.setValue("Despesa");
        datePicker.setValue(LocalDate.now());
    }
}
