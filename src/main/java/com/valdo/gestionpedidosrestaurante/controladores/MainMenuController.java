package com.valdo.gestionpedidosrestaurante.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private void abrirClientes() {
        try {
            // Carga el archivo FXML de Gestión de Clientes
            Parent root = FXMLLoader.load(getClass().getResource("/com/valdo/gestionpedidosrestaurante/gestion-clientes.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Gestión de Clientes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la ventana de Gestión de Clientes.");
        }
    }

    @FXML
    private void abrirProductos() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/valdo/gestionpedidosrestaurante/gestion-productos.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Gestión de Productos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la ventana de Gestión de Productos.");
        }
    }

    @FXML
    private void abrirPedidos() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/valdo/gestionpedidosrestaurante/gestion-pedidos.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Gestión de Pedidos");
            stage.setScene(new Scene(root, 1000, 900));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la ventana de Gestión de Pedidos..");
        }
    }
}
