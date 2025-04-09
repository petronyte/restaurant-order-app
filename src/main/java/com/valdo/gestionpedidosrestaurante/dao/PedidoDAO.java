package com.valdo.gestionpedidosrestaurante.dao;

import com.valdo.gestionpedidosrestaurante.database.DatabaseConnection;
import com.valdo.gestionpedidosrestaurante.modelos.Pedido;
import com.valdo.gestionpedidosrestaurante.modelos.Cliente;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public static void agregarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (cliente_id, fecha, hora, total, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pedido.getCliente().getId());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setTime(3, Time.valueOf(pedido.getHora()));
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pedido.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void modificarPedido(Pedido pedido) {
        String sql = "UPDATE pedidos SET cliente_id = ?, fecha = ?, hora = ?, total = ?, estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getCliente().getId());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setTime(3, Time.valueOf(pedido.getHora()));
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado());
            stmt.setInt(6, pedido.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Pedido> buscarPedidos(String criterio) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.id AS pedido_id, p.fecha, p.hora, p.total, p.estado, " +
                "c.id AS cliente_id, c.nombre, c.telefono, c.direccion " +
                "FROM pedidos p JOIN clientes c ON p.cliente_id = c.id WHERE c.nombre LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + criterio + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                );
                Pedido pedido = new Pedido(
                        rs.getInt("pedido_id"),
                        cliente,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getDouble("total"),
                        rs.getString("estado")
                );
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public static List<Pedido> obtenerPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.id AS pedido_id, p.fecha, p.hora, p.total, p.estado, " +
                "c.id AS cliente_id, c.nombre, c.telefono, c.direccion " +
                "FROM pedidos p JOIN clientes c ON p.cliente_id = c.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                );
                Pedido pedido = new Pedido(
                        rs.getInt("pedido_id"),
                        cliente,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getDouble("total"),
                        rs.getString("estado")
                );
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public static void eliminarPedido(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
