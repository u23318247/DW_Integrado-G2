package com.utp.tienda.model;

/** Tipo de movimiento de inventario registrado en la bitacora de stock. */
public enum TipoMovimiento {

    /** Ingreso de mercaderia (compra, devolucion de cliente). */
    ENTRADA,

    /** Egreso de mercaderia (venta, consumo interno). */
    SALIDA,

    /** Ajuste manual que incrementa el stock (merma sobrante, conteo fisico). */
    AJUSTE_POSITIVO,

    /** Ajuste manual que reduce el stock (producto danado, vencimiento). */
    AJUSTE_NEGATIVO
}