package com.utp.semanados.dto;
public record ProductoRequest(
String nombre,
double precio,
int stock
) {
}