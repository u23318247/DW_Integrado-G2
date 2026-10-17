package com.utp.tienda.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.tienda.service.ReporteService;

/**
 * Reto de aplicacion: protegido para ROLE_ADMIN en SecurityConfig (/api/admin/**).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReporteService reporteService;

    public AdminController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reporte")
    public Map<String, Object> reporte() {
        return reporteService.generarReporte();
    }
}
