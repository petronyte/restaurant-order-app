module com.valdo.gestionpedidosrestaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires net.sf.jasperreports.core;

    // Exporta los paquetes para JavaFX
    exports com.valdo.gestionpedidosrestaurante.controladores to javafx.fxml;
    exports com.valdo.gestionpedidosrestaurante to javafx.graphics;

    // Abre paquetes para uso reflexivo por JavaFX
    opens com.valdo.gestionpedidosrestaurante.controladores to javafx.fxml;
    opens com.valdo.gestionpedidosrestaurante to javafx.fxml;
}
