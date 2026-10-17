package com.utp.tienda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utp.tienda.exception.ReglaNegocioException;
import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;

/**
 * Actividad de consolidacion: modulo de inventario con dos niveles de acceso.
 * GET  /api/inventario/**       -> ROLE_USER o ROLE_ADMIN
 * POST /api/inventario/ajustes  -> solo ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ProductoService service;

    public InventarioController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo(@RequestParam(defaultValue = "5") Integer limite) {
        return service.buscarConStockBajo(limite);
    }

    @PostMapping("/ajustes")
    public Producto ajustar(@RequestBody AjusteRequest ajuste) {
        if (ajuste.productoId() == null || ajuste.cantidad() == null || ajuste.tipo() == null) {
            throw new ReglaNegocioException("productoId, tipo y cantidad son obligatorios");
        }
        return switch (ajuste.tipo().toUpperCase()) {
            case "ENTRADA" -> service.registrarEntrada(ajuste.productoId(), ajuste.cantidad());
            case "SALIDA" -> service.registrarSalida(ajuste.productoId(), ajuste.cantidad());
            default -> throw new ReglaNegocioException("El tipo debe ser ENTRADA o SALIDA");
        };
    }

    /** Cuerpo del ajuste: {"productoId":1,"tipo":"ENTRADA","cantidad":3}. */
    public record AjusteRequest(Long productoId, String tipo, Integer cantidad) {
    }
}
