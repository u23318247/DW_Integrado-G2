package com.utp.semanas2.dto;

public record ProductoRequest(
    String nombre,
    double precio,
    int stock
) {}
