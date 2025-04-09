package com.valdo.gestionpedidosrestaurante.dao;

import com.valdo.gestionpedidosrestaurante.database.DatabaseConnection;
import com.valdo.gestionpedidosrestaurante.modelos.DetallePedido;
import com.valdo.gestionpedidosrestaurante.modelos.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    public static void agregarDetallePedido(DetallePedido detalle) {
        String sql = "INSERT INTO detallepedidos (pedido_id, producto_id, cantidad, precio, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, detalle.getIdPedido());
            stmt.setInt(2, detalle.getProducto().getId());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setDouble(4, detalle.getPrecio());
            stmt.setDouble(5, detalle.getSubtotal());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                detalle.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<DetallePedido> obtenerDetallesPorPedido(int pedidoId) {
        List<DetallePedido> detalles = new ArrayList<>();
        String sql = "SELECT dp.id AS detalle_id, dp.cantidad, dp.precio, dp.subtotal, " +
                "p.id AS producto_id, p.nombre, p.categoria, p.precio AS precio_producto, p.disponible " +
                "FROM detallepedidos dp JOIN productos p ON dp.producto_id = p.id " +
                "WHERE dp.pedido_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("producto_id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio_producto"),
                        rs.getBoolean("disponible")
                );
                DetallePedido detalle = new DetallePedido(
                        rs.getInt("detalle_id"),
                        pedidoId,
                        producto,
                        rs.getInt("cantidad"),
                        rs.getDouble("precio")
                );
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }
}