package com.proyectomdweb.proyectomdweb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DisminuirStockRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad a disminuir debe ser mayor a cero")
    private Integer cantidad;

    public DisminuirStockRequest() {
    }

    public DisminuirStockRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
