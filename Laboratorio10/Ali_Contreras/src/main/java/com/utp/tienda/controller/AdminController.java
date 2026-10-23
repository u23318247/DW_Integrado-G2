package com.utp.tienda.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.tienda.dto.ResumenBackendResponse;
import com.utp.tienda.service.AdminResumenService;
import com.utp.tienda.service.ReporteService;

/**
 * Endpoints administrativos, protegidos para ROLE_ADMIN en SecurityConfig (/api/admin/**).
 * /reporte: reto de la semana 9. /resumen: resumen integrado de la semana 10.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReporteService reporteService;
    private final AdminResumenService resumenService;

    public AdminController(ReporteService reporteService, AdminResumenService resumenService) {
        this.reporteService = reporteService;
        this.resumenService = resumenService;
    }

    @GetMapping("/reporte")
    public Map<String, Object> reporte() {
        return reporteService.generarReporte();
    }

    @GetMapping("/resumen")
    public ResumenBackendResponse resumen() {
        return resumenService.obtenerResumen();
    }
}
