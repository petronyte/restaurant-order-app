package com.valdo.gestionpedidosrestaurante.controladores;

import com.valdo.gestionpedidosrestaurante.dao.ProductoDAO;
import com.valdo.gestionpedidosrestaurante.database.DatabaseConnection;
import com.valdo.gestionpedidosrestaurante.modelos.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GestionProductosController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtPrecio;
    @FXML private CheckBox chkDisponible;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colID;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Boolean> colDisponible;
    @FXML private Button btnVolver;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        colID.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getId()));
        colNombre.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getNombre()));
        colCategoria.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getCategoria()));
        colPrecio.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getPrecio()));
        colDisponible.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().isDisponible()));

        actualizarListaProductos();

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtNombre.setText(newSelection.getNombre());
                txtCategoria.setText(newSelection.getCategoria());
                txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                chkDisponible.setSelected(newSelection.isDisponible());
            }
        });
    }

    private void actualizarListaProductos() {
        listaProductos.setAll(ProductoDAO.obtenerProductos());
        tblProductos.setItems(listaProductos);
    }

    @FXML
    private void agregarProducto() {
        if (txtNombre.getText().isEmpty() || txtCategoria.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un número válido.");
            return;
        }

        ProductoDAO.agregarProducto(txtNombre.getText(), txtCategoria.getText(), precio, chkDisponible.isSelected());
        actualizarListaProductos();
        limpiarCampos();
    }

    @FXML
    private void modificarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto para modificar.");
            return;
        }

        if (txtNombre.getText().isEmpty() || txtCategoria.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un número válido.");
            return;
        }

        productoSeleccionado.setNombre(txtNombre.getText());
        productoSeleccionado.setCategoria(txtCategoria.getText());
        productoSeleccionado.setPrecio(precio);
        productoSeleccionado.setDisponible(chkDisponible.isSelected());

        ProductoDAO.modificarProducto(productoSeleccionado);
        actualizarListaProductos();
        limpiarCampos();
    }

    @FXML
    private void buscarProducto() {
        String nombreBuscado = txtNombre.getText().trim();
        if (nombreBuscado.isEmpty()) {
            mostrarAlerta("Ingrese un nombre para buscar.");
            return;
        }

        listaProductos.setAll(ProductoDAO.buscarProductos(nombreBuscado));
        tblProductos.setItems(listaProductos);
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto para eliminar.");
            return;
        }

        ProductoDAO.eliminarProducto(productoSeleccionado.getId());
        actualizarListaProductos();
        limpiarCampos();
    }

    @FXML
    private void volverAlMenu() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCategoria.clear();
        txtPrecio.clear();
        chkDisponible.setSelected(false);
        productoSeleccionado = null;
    }

    @FXML
    private void generarReporteProductosBajoPrecio() {
        try {
            // Configurar el nivel de logging para JasperReports a SEVERE para que no se muestren advertencias
            java.util.logging.Logger.getLogger("net.sf.jasperreports").setLevel(java.util.logging.Level.SEVERE);

            // Cargar el reporte desde el classpath usando la ruta absoluta
            InputStream reporteStream = getClass().getResourceAsStream(
                    "/com/valdo/gestionpedidosrestaurante/reports/ProductosBajoPrecio.jasper");
            if (reporteStream == null) {
                throw new JRException("No se encontró el reporte ProductosBajoPrecio.jasper en la ruta especificada.");
            }

            // Obtener la conexión a la base de datos
            Connection conexion = DatabaseConnection.getConnection();
            if (conexion == null) {
                mostrarAlerta("Error al conectar a la base de datos");
                return;
            }

            Map<String, Object> parametros = new HashMap<>();

            // Rellenar el reporte usando el InputStream, los parámetros y la conexión
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conexion);

            // Mostrar el reporte en una ventana (JasperViewer utiliza Swing)
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al generar el informe de productos (precio < 5): " + e.getMessage());
        }
    }


    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
