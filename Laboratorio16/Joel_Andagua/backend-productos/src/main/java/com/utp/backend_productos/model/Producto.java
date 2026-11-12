package com.utp.backend_productos.model;

import java.time.LocalDate;

public class Producto {
    private Long id;
    private String nombre;
    private double precio;
    private int stock;
    private LocalDate fechaRegistro;

    public Producto() {}

    public Producto(Long id, String nombre, double precio, int stock, LocalDate fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
