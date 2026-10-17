package com.utp.tienda.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.tienda.service.ProductoService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductoService productoService;

    public AdminController(
            ProductoService productoService) {

        this.productoService = productoService;
    }

    @GetMapping("/reporte")
    public Map<String, Object> reporte() {

        return Map.of(
                "fechaServidor",
                LocalDateTime.now(),

                "totalProductos",
                productoService.contar()
        );
    }
}