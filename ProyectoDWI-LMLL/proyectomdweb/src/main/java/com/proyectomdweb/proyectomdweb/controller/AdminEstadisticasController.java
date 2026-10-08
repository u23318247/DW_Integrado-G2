package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.dtos.GraficaDataDTO;
import com.proyectomdweb.proyectomdweb.service.PresentacionDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/estadisticas")
@RequiredArgsConstructor
public class AdminEstadisticasController {

    private final PresentacionDatosService presentacionDatosService;

    // --- 1. RUTA PARA LA VISTA HTML --- //
    @GetMapping
    public String mostrarPanelIngresos() {
        return "admin/panel-ingresos"; 
    }

    // --- 2. RUTAS PARA EL JAVASCRIPT (AJAX) --- //
    @GetMapping("/api/ingresos/mensual")
    @ResponseBody // <--- Obliga a Spring a devolver JSON en lugar de buscar un HTML
    public List<GraficaDataDTO> getMensual(@RequestParam int anio, @RequestParam int mes) {
        return presentacionDatosService.obtenerIngresosMensuales(anio, mes);
    }

    @GetMapping("/api/ingresos/anual")
    @ResponseBody 
    public List<GraficaDataDTO> getAnual(@RequestParam int anio) {
        return presentacionDatosService.obtenerIngresosAnuales(anio);
    }
}