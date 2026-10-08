package com.proyectomdweb.proyectomdweb.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutRequestDTO(
    String clienteNombre,
    String clienteTelefono,
    String clienteDireccion,
    String metodoPago,
    BigDecimal total,
    List<ItemCarritoDTO> items
) {}

// Este record luego lo tengo que pasar a un archivo ItemCarritoDTO.java esepro no olvidarme
record ItemCarritoDTO(
    String nombre,
    BigDecimal precio,
    Integer cantidad
) {}
