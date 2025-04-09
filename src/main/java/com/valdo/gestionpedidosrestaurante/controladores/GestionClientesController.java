package com.valdo.gestionpedidosrestaurante.controladores;

import com.valdo.gestionpedidosrestaurante.dao.ClienteDAO;
import com.valdo.gestionpedidosrestaurante.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.valdo.gestionpedidosrestaurante.modelos.Cliente;
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

public class GestionClientesController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colID;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private Button btnVolverClientes;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @FXML
    private void initialize() {
        // Configuración de columnas
        colID.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getId()));
        colNombre.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getNombre()));
        colTelefono.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getTelefono()));
        colDireccion.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getDireccion()));

        // Carga inicial de datos
        actualizarListaClientes();

        // Manejar selección en la tabla
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                txtNombre.setText(newSelection.getNombre());
                txtTelefono.setText(newSelection.getTelefono());
                txtDireccion.setText(newSelection.getDireccion());
            }
        });
    }

    private void actualizarListaClientes() {
        listaClientes.setAll(ClienteDAO.obtenerClientes());
        tblClientes.setItems(listaClientes);
    }

    @FXML
    private void agregarCliente() {
        if (txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }
        ClienteDAO.agregarCliente(txtNombre.getText(), txtTelefono.getText(), txtDireccion.getText());
        actualizarListaClientes();
        limpiarCampos();
    }

    @FXML
    private void modificarCliente() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Seleccione un cliente para modificar.");
            return;
        }
        clienteSeleccionado.setNombre(txtNombre.getText());
        clienteSeleccionado.setTelefono(txtTelefono.getText());
        clienteSeleccionado.setDireccion(txtDireccion.getText());

        ClienteDAO.modificarCliente(clienteSeleccionado);
        actualizarListaClientes();
        limpiarCampos();
    }

    @FXML
    private void buscarCliente() {
        String nombreBuscado = txtNombre.getText().trim();
        listaClientes.setAll(ClienteDAO.buscarClientes(nombreBuscado));
        tblClientes.setItems(listaClientes);
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Seleccione un cliente para eliminar.");
            return;
        }
        ClienteDAO.eliminarCliente(clienteSeleccionado.getId());
        actualizarListaClientes();
        limpiarCampos();
    }

    @FXML
    private void volverAlMenu() {
        Stage stage = (Stage) btnVolverClientes.getScene().getWindow();
        stage.close();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        clienteSeleccionado = null;
    }

    /**
     * Genera y muestra el informe de "Clientes del restaurante" utilizando JasperReports.
     */
    @FXML
    private void generarReporteClientes() {
        // Configurar el nivel de logging para JasperReports a SEVERE para que no se muestren advertencias
        java.util.logging.Logger.getLogger("net.sf.jasperreports").setLevel(java.util.logging.Level.SEVERE);

        try {
            InputStream reporteStream = getClass().getResourceAsStream(
                    "/com/valdo/gestionpedidosrestaurante/reports/Clientes.jasper");
            if (reporteStream == null) {
                throw new JRException("No se encontró el reporte Clientes.jasper en la ruta especificada.");
            }

            Connection conexion = DatabaseConnection.getConnection();
            if (conexion == null) {
                mostrarAlerta("Error al conectar a la base de datos");
                return;
            }

            Map<String, Object> parametros = new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conexion);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al generar el informe de clientes: " + e.getMessage());
        }
    }




    /**
     * Muestra una alerta de error.
     * @param mensaje Mensaje a mostrar.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
