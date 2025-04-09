package com.valdo.gestionpedidosrestaurante.modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Pedido {
    private int id;
    private Cliente cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private double total;
    private String estado;

    public Pedido(int id, Cliente cliente, LocalDate fecha, LocalTime hora, double total, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.total = total;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
