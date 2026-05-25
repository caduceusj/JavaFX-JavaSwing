package com.example.javafx.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {

    public enum Type { RECEITA, DESPESA }

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final StringProperty  category = new SimpleStringProperty();
    private final DoubleProperty  amount = new SimpleDoubleProperty();
    private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Transaction(int id, String description, String category, double amount, Type type, LocalDate date) {
        this.id.set(id);
        this.description.set(description);
        this.category.set(category);
        this.amount.set(amount);
        this.type.set(type);
        this.date.set(date);
    }

    // Property accessors (for TableView binding)
    public IntegerProperty idProperty()          { return id; }
    public StringProperty  descriptionProperty() { return description; }
    public StringProperty  categoryProperty()    { return category; }
    public DoubleProperty  amountProperty()      { return amount; }
    public ObjectProperty<Type> typeProperty()   { return type; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    // Plain getters
    public int       getId()          { return id.get(); }
    public String    getDescription() { return description.get(); }
    public String    getCategory()    { return category.get(); }
    public double    getAmount()      { return amount.get(); }
    public Type      getType()        { return type.get(); }
    public LocalDate getDate()        { return date.get(); }
    public String    getDateStr()     { return date.get().format(FMT); }

    // Plain setters
    public void setId(int v)             { id.set(v); }
    public void setDescription(String v) { description.set(v); }
    public void setCategory(String v)    { category.set(v); }
    public void setAmount(double v)      { amount.set(v); }
    public void setType(Type v)          { type.set(v); }
    public void setDate(LocalDate v)     { date.set(v); }

    public boolean isReceita() { return type.get() == Type.RECEITA; }
}
