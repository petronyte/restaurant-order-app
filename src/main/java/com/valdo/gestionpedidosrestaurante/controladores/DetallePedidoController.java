package com.valdo.gestionpedidosrestaurante.controladores;

import com.valdo.gestionpedidosrestaurante.dao.DetallePedidoDAO;
import com.valdo.gestionpedidosrestaurante.modelos.DetallePedido;
import com.valdo.gestionpedidosrestaurante.modelos.Pedido;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DetallePedidoController {

    @FXML
    private Label lblCliente;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblHora;
    @FXML
    private Label lblEstado;

    @FXML
    private TableView<DetallePedido> tblDetalles;
    @FXML
    private TableColumn<DetallePedido, String> colProducto;
    @FXML
    private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML
    private TableColumn<DetallePedido, Double> colPrecioUnitario;
    @FXML
    private TableColumn<DetallePedido, Double> colSubtotal;

    // Lista que contendrá los detalles del pedido
    private ObservableList<DetallePedido> listaDetalles = FXCollections.observableArrayList();

    // Pedido cuyo detalle se mostrará
    private Pedido pedido;

    /**
     * Método para recibir el pedido seleccionado desde la ventana principal.
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
        cargarDatosPedido();
        cargarDetalles();
    }

    /**
     * Carga la información general del pedido en los labels.
     */
    private void cargarDatosPedido() {
        if (pedido != null) {
            lblCliente.setText(pedido.getCliente().getNombre());
            lblFecha.setText(pedido.getFecha().toString());
            lblHora.setText(pedido.getHora().toString());
            lblEstado.setText(pedido.getEstado());
        }
    }

    /**
     * Carga los detalles del pedido usando el DAO y los muestra en la tabla.
     */
    private void cargarDetalles() {
        if (pedido != null) {
            List<DetallePedido> detalles = DetallePedidoDAO.obtenerDetallesPorPedido(pedido.getId());
            listaDetalles.setAll(detalles);
            tblDetalles.setItems(listaDetalles);
        }
    }

    /**
     * Inicialización del controlador. Configura las columnas de la tabla.
     */
    @FXML
    private void initialize() {
        colProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        colPrecioUnitario.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        colSubtotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());
    }

    /**
     * Método para cerrar la ventana de detalles.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) tblDetalles.getScene().getWindow();
        stage.close();
    }
}
