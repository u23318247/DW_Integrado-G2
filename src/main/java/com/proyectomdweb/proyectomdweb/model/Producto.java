package com.proyectomdweb.proyectomdweb.model;

public class Producto {
    private Long id;
    private String nombre;
    private String categoria;
    private String genero;
    private String imagenUrl;
    private Double precio;
    private Integer stock;
    private Boolean disponible;

    public Producto() {
    }

    public Producto(Long id, String nombre, String categoria, String genero, String imagenUrl, Double precio, Integer stock, Boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.genero = genero;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.stock = stock;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}
