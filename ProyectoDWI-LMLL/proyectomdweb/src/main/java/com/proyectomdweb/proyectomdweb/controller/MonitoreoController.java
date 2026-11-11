package com.proyectomdweb.proyectomdweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de Dashboard Visual de Observabilidad y Métricas
 * Convierte los datos JSON de Actuator en una interfaz gráfica interactiva con gráficos y estado en tiempo real.
 * Alumna: Antonela Jaimes
 */
@Controller
public class MonitoreoController {

    @GetMapping("/monitoreo")
    public String verDashboard(Model model) {
        model.addAttribute("titulo", "Dashboard de Observabilidad y Monitoreo del Servidor");
        return "monitoreo";
    }
}
