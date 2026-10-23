package com.utp.tienda.dto;

/** Respuesta del resumen administrativo (semana 10). */
public record ResumenBackendResponse(
        long totalProductos,
        long productosConStockBajo,
        long totalMovimientos,
        String estado) {
}
