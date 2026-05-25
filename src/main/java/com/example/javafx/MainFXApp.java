package com.example.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX Personal Finance Dashboard.
 *
 * Run via Maven:
 *   mvn javafx:run
 *
 * Or in Eclipse: Run As > Java Application (select this class).
 * Note: Eclipse may need the JavaFX VM arguments configured (see README).
 */
public class MainFXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/example/javafx/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 720);
        scene.getStylesheets().add(
            getClass().getResource("/com/example/javafx/css/style.css").toExternalForm());

        stage.setTitle("FinanceFlow — Dashboard Financeiro Pessoal");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
