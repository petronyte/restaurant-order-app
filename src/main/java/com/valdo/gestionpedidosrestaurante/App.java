package com.valdo.gestionpedidosrestaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga el archivo FXML desde la carpeta resources
        Parent root = FXMLLoader.load(getClass().getResource("/com/valdo/gestionpedidosrestaurante/main-menu.fxml"));
        primaryStage.setTitle("Sistema de Gestión de Pedidos");
        primaryStage.setScene(new Scene(root, 800, 450)); // Tamaño inicial de la ventana
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Inicia la aplicación JavaFX
    }
}
