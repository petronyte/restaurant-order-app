package com.valdo.gestionpedidosrestaurante.controladores;

import com.valdo.gestionpedidosrestaurante.dao.DetallePedidoDAO;
import com.valdo.gestionpedidosrestaurante.dao.PedidoDAO;
import com.valdo.gestionpedidosrestaurante.dao.ClienteDAO;
import com.valdo.gestionpedidosrestaurante.dao.ProductoDAO;
import com.valdo.gestionpedidosrestaurante.database.DatabaseConnection;
import com.valdo.gestionpedidosrestaurante.modelos.DetallePedido;
import com.valdo.gestionpedidosrestaurante.modelos.Pedido;
import com.valdo.gestionpedidosrestaurante.modelos.Cliente;
import com.valdo.gestionpedidosrestaurante.modelos.Producto;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionPedidosController {

    @FXML private ComboBox<Cliente> cmbClientes;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, Integer> colIdPedido;
    @FXML private TableColumn<Pedido, String> colCliente;
    @FXML private TableColumn<Pedido, LocalDate> colFecha;
    @FXML private TableColumn<Pedido, LocalTime> colHora;
    @FXML private TableColumn<Pedido, Double> colTotal;
    @FXML private TableColumn<Pedido, String> colEstado;

    private ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private Pedido pedidoSeleccionado;

    // Nuevos elementos del detalle del pedido
    @FXML private ComboBox<Producto> cmbProductos;
    @FXML private TextField txtCantidad;
    @FXML private TableView<DetallePedido> tblDetallePedido;
    @FXML private TableColumn<DetallePedido, String> colProducto;
    @FXML private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML private TableColumn<DetallePedido, Double> colPrecioUnitario;
    @FXML private TableColumn<DetallePedido, Double> colSubtotal;

    // Lista para almacenar los detalles del pedido
    private ObservableList<DetallePedido> listaDetallePedido = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        // Configuración de las columnas de la tabla
        colIdPedido.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getId()));
        colCliente.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getCliente().getNombre()));
        colFecha.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getFecha()));
        colHora.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getHora()));
        colTotal.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getTotal()));
        colEstado.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getEstado()));

        // Cargar datos iniciales
        cargarClientes();
        cargarEstados();
        actualizarListaPedidos();

        // Manejo de selección en la tabla
        tblPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                pedidoSeleccionado = newSelection;
                cmbClientes.setValue(newSelection.getCliente());
                dpFecha.setValue(newSelection.getFecha());
                txtHora.setText(newSelection.getHora().toString());
                txtTotal.setText(String.valueOf(newSelection.getTotal()));
                cmbEstado.setValue(newSelection.getEstado());
            }
        });

        // Cargar productos desde la base de datos (utilizando ProductoDAO)
        List<Producto> productos = ProductoDAO.obtenerProductos();
        cmbProductos.setItems(FXCollections.observableArrayList(productos));

        // Configurar columnas de la tabla de detalles
        colProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        colPrecioUnitario.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        colSubtotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        tblDetallePedido.setItems(listaDetallePedido);
    }

    private void cargarClientes() {
        List<Cliente> clientes = ClienteDAO.obtenerClientes();
        cmbClientes.setItems(FXCollections.observableArrayList(clientes));
    }

    private void cargarEstados() {
        cmbEstado.setItems(FXCollections.observableArrayList("Pendiente", "En Preparación", "Completado", "Cancelado"));
    }

    private void actualizarListaPedidos() {
        listaPedidos.setAll(PedidoDAO.obtenerPedidos());
        tblPedidos.setItems(listaPedidos);
    }

    @FXML
    private void crearPedido() {
        if(cmbClientes.getValue() == null || dpFecha.getValue() == null ||
                txtHora.getText().isEmpty() || cmbEstado.getValue() == null) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        // Validar que txtTotal contenga un valor numérico
        double total = 0.0;
        if (!txtTotal.getText().isEmpty()) {
            try {
                total = Double.parseDouble(txtTotal.getText());
            } catch(NumberFormatException e) {
                mostrarAlerta("El total debe ser un número válido.");
                return;
            }
        } else {
            // Puedes asignar un valor por defecto o mostrar un mensaje de alerta
            mostrarAlerta("Agrega al menos un producto al pedido para calcular el total.");
            return;
        }

        Pedido pedido = new Pedido(0, cmbClientes.getValue(), dpFecha.getValue(),
                LocalTime.parse(txtHora.getText()), total, cmbEstado.getValue());

        // Insertar el pedido en la base de datos
        PedidoDAO.agregarPedido(pedido);

        // Insertar los detalles del pedido
        for (DetallePedido detalle : listaDetallePedido) {
            detalle.setIdPedido(pedido.getId());
            DetallePedidoDAO.agregarDetallePedido(detalle);
        }

        actualizarListaPedidos();
        limpiarCampos();
        listaDetallePedido.clear();
    }


    @FXML
    private void modificarPedido() {
        if(pedidoSeleccionado == null) {
            mostrarAlerta("Seleccione un pedido para modificar.");
            return;
        }
        pedidoSeleccionado.setCliente(cmbClientes.getValue());
        pedidoSeleccionado.setFecha(dpFecha.getValue());
        pedidoSeleccionado.setHora(LocalTime.parse(txtHora.getText()));
        // El total se recalculará según los detalles
        pedidoSeleccionado.setEstado(cmbEstado.getValue());
        PedidoDAO.modificarPedido(pedidoSeleccionado);
        actualizarListaPedidos();
        limpiarCampos();
    }

    @FXML
    private void buscarPedido() {
        String criterio = "";
        if(cmbClientes.getValue() != null) {
            criterio = cmbClientes.getValue().getNombre();
        }
        listaPedidos.setAll(PedidoDAO.buscarPedidos(criterio));
        tblPedidos.setItems(listaPedidos);
    }

    @FXML
    private void eliminarPedido() {
        if(pedidoSeleccionado == null) {
            mostrarAlerta("Seleccione un pedido para eliminar.");
            return;
        }
        // Se puede eliminar primero los detalles asociados:
        // DetallePedidoDAO.eliminarDetallesPorPedido(pedidoSeleccionado.getId());
        PedidoDAO.eliminarPedido(pedidoSeleccionado.getId());
        actualizarListaPedidos();
        limpiarCampos();
    }

    @FXML
    private void volverAlMenu() {
        Stage stage = (Stage) tblPedidos.getScene().getWindow();
        stage.close();
    }

    private void limpiarCampos() {
        cmbClientes.setValue(null);
        dpFecha.setValue(null);
        txtHora.clear();
        txtTotal.clear();
        cmbEstado.setValue(null);
        pedidoSeleccionado = null;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizarTotal() {
        double total = 0;
        for (DetallePedido detalle : listaDetallePedido) {
            total += detalle.getSubtotal();
        }
        txtTotal.setText(String.valueOf(total));
    }

    private void limpiarCamposDetalle() {
        cmbProductos.setValue(null);
        txtCantidad.clear();
    }

    @FXML
    private void agregarProductoAlPedido() {
        Producto productoSeleccionado = cmbProductos.getValue();
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                mostrarAlerta("La cantidad debe ser mayor que 0.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese una cantidad válida.");
            return;
        }

        // Crear un nuevo detalle para el pedido. Nota: El id del pedido se asignará al guardar el pedido.
        DetallePedido detalle = new DetallePedido(0, 0, productoSeleccionado, cantidad, productoSeleccionado.getPrecio());
        listaDetallePedido.add(detalle);

        // Actualizar el total del pedido (suma de todos los subtotales)
        actualizarTotal();

        // Limpiar campos de selección de producto y cantidad
        limpiarCamposDetalle();
    }

    @FXML
    private void verDetallePedido() {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Seleccione un pedido para ver sus detalles.");
            return;
        }

        try {
            // Cargar la nueva ventana de detalles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/valdo/gestionpedidosrestaurante/detalle-pedido.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la ventana de detalles
            DetallePedidoController controller = loader.getController();
            // Pasar el pedido seleccionado para que el controlador pueda cargar sus detalles
            controller.setPedido(pedidoSeleccionado);

            // Configurar y mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Detalles del Pedido");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir la ventana de detalles.");
        }
    }
    @FXML
    private void generarReportePedidosEnPreparacion() {
        try {
            // Configurar el nivel de logging para JasperReports a SEVERE para que no se muestren advertencias
            java.util.logging.Logger.getLogger("net.sf.jasperreports").setLevel(java.util.logging.Level.SEVERE);

            InputStream reporteStream = getClass().getResourceAsStream(
                    "/com/valdo/gestionpedidosrestaurante/reports/PedidosEnPreparacion.jasper");
            if (reporteStream == null) {
                throw new JRException("No se encontró el reporte PedidosEnPreparacion.jasper en la ruta especificada.");
            }

            // Obtener la conexión a la base de datos.
            Connection conexion = DatabaseConnection.getConnection();
            if (conexion == null) {
                mostrarAlerta("Error al conectar a la base de datos");
                return;
            }

            // Se crea un mapa de parámetros si se requieren; en este caso, no se utilizan
            Map<String, Object> parametros = new HashMap<>();

            // Rellenar el reporte usando el InputStream, parámetros y la conexión.
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conexion);

            // Mostrar el reporte en una ventana (JasperViewer utiliza Swing).
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al generar el informe de pedidos en preparación: " + e.getMessage());
        }
    }

    @FXML
    public void generarTicketPedido(javafx.event.ActionEvent actionEvent) {
        // Configurar el nivel de logging para JasperReports a SEVERE para que no se muestren advertencias
        java.util.logging.Logger.getLogger("net.sf.jasperreports").setLevel(java.util.logging.Level.SEVERE);

        // Obtener el pedido seleccionado en la tabla
        Pedido pedidoSeleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Advertencia: Debes seleccionar un pedido antes de generar el ticket.");
            return;
        }
        int idPedido = pedidoSeleccionado.getId(); // Obtener el ID del pedido

        Connection conexion = null;
        try {
            // Rutas de los reportes (ajustadas a la estructura de tu proyecto)
            String reportPath = "C:\\Users\\Valdo\\Desktop\\IdeaProyects\\GestionPedidosRestaurante\\src\\main\\resources\\com\\valdo\\gestionpedidosrestaurante\\reports\\ticketPedido.jasper";
            String subreportPath = "C:\\Users\\Valdo\\Desktop\\IdeaProyects\\GestionPedidosRestaurante\\src\\main\\resources\\com\\valdo\\gestionpedidosrestaurante\\reports\\detallePedido.jasper";

            // Conexión a la base de datos
            conexion = DatabaseConnection.getConnection();
            if (conexion == null) {
                mostrarAlerta("Error: No se pudo conectar a la base de datos.");
                return;
            }

            // Verificar que los archivos de los reportes existen
            File reportFile = new File(reportPath);
            File subreportFile = new File(subreportPath);
            if (!reportFile.exists() || !subreportFile.exists()) {
                mostrarAlerta("Error: No se encontró el archivo del informe o el subreporte.");
                return;
            }

            // Cargar el reporte maestro
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);

            // Enviar el ID del pedido como parámetro y la ruta del subreporte
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("idPedido", idPedido);
            // Aquí se utiliza el directorio del subreporte; en Windows se usa "\\" al final.
            parameters.put("SUBREPORT_DIR", subreportFile.getParent() + "\\");

            // Llenar el informe con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);

            // Mostrar el ticket en JasperViewer
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
            mostrarAlerta("Error: No se pudo generar el ticket. Revisa los parámetros y el informe.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
