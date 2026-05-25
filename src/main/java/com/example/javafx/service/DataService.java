package com.example.javafx.service;

import com.example.javafx.model.Transaction;
import com.example.javafx.model.Transaction.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {

    private static DataService instance;
    private final ObservableList<Transaction> transactions;
    private int nextId = 100;

    private DataService() {
        transactions = FXCollections.observableArrayList(buildSampleData());
    }

    public static DataService get() {
        if (instance == null) instance = new DataService();
        return instance;
    }

    public ObservableList<Transaction> getTransactions() { return transactions; }

    public void addTransaction(Transaction t) {
        t.setId(nextId++);
        transactions.add(t);
    }

    public void removeTransaction(Transaction t) { transactions.remove(t); }

    public double getTotalReceita() {
        return transactions.stream().filter(Transaction::isReceita).mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalDespesa() {
        return transactions.stream().filter(t -> !t.isReceita()).mapToDouble(Transaction::getAmount).sum();
    }

    public double getSaldo() { return getTotalReceita() - getTotalDespesa(); }

    public Map<String, Double> getDespesasByCategory() {
        return transactions.stream()
            .filter(t -> !t.isReceita())
            .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }

    public Map<String, Double> getBalanceByMonth() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getDate().getYear() + "/" + String.format("%02d", t.getDate().getMonthValue()),
                Collectors.summingDouble(t -> t.isReceita() ? t.getAmount() : -t.getAmount())
            ));
    }

    private Transaction[] buildSampleData() {
        LocalDate now = LocalDate.now();
        return new Transaction[]{
            new Transaction(1,  "Salário mensal",        "Salário",         6500.00, Type.RECEITA, now.minusDays(1)),
            new Transaction(2,  "Freelance design",      "Renda Extra",     1200.00, Type.RECEITA, now.minusDays(5)),
            new Transaction(3,  "Supermercado",          "Alimentação",      580.50, Type.DESPESA, now.minusDays(2)),
            new Transaction(4,  "Aluguel",               "Moradia",         1500.00, Type.DESPESA, now.minusDays(3)),
            new Transaction(5,  "Conta de luz",          "Moradia",          145.80, Type.DESPESA, now.minusDays(4)),
            new Transaction(6,  "Academia",              "Saúde",            89.90,  Type.DESPESA, now.minusDays(6)),
            new Transaction(7,  "Netflix",               "Lazer",            39.90,  Type.DESPESA, now.minusDays(7)),
            new Transaction(8,  "Restaurante",           "Alimentação",     120.00,  Type.DESPESA, now.minusDays(8)),
            new Transaction(9,  "Farmácia",              "Saúde",            67.40,  Type.DESPESA, now.minusDays(9)),
            new Transaction(10, "Transporte / Uber",     "Transporte",      200.00,  Type.DESPESA, now.minusDays(10)),
            new Transaction(11, "Combustível",           "Transporte",      280.00,  Type.DESPESA, now.minusDays(11)),
            new Transaction(12, "Curso online",          "Educação",        149.00,  Type.DESPESA, now.minusDays(12)),
            new Transaction(13, "Investimento CDB",      "Investimentos",   500.00,  Type.DESPESA, now.minusDays(13)),
            new Transaction(14, "Rendimento CDB",        "Renda Extra",      48.50,  Type.RECEITA, now.minusDays(14)),
            new Transaction(15, "Roupa e calçados",      "Vestuário",       320.00,  Type.DESPESA, now.minusDays(15)),
            new Transaction(16, "Internet",              "Moradia",          89.90,  Type.DESPESA, now.minusDays(16)),
            new Transaction(17, "Plano de saúde",        "Saúde",           380.00,  Type.DESPESA, now.minusDays(17)),
            new Transaction(18, "Venda de produto",      "Renda Extra",     350.00,  Type.RECEITA, now.minusMonths(1).plusDays(2)),
            new Transaction(19, "Salário mês anterior",  "Salário",        6500.00,  Type.RECEITA, now.minusMonths(1).plusDays(1)),
            new Transaction(20, "Supermercado",          "Alimentação",     610.00,  Type.DESPESA, now.minusMonths(1).plusDays(3)),
            new Transaction(21, "Aluguel",               "Moradia",        1500.00,  Type.DESPESA, now.minusMonths(1).plusDays(4)),
            new Transaction(22, "Viagem / Passeio",      "Lazer",           850.00,  Type.DESPESA, now.minusMonths(1).plusDays(10)),
        };
    }
}
