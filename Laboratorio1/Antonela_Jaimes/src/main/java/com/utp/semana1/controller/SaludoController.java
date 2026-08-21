package com.utp.semana1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.utp.semana1.model.ProyectoInfo;
import com.utp.semana1.model.EstadoInfo;

@RestController
@RequestMapping("/api")
public class SaludoController {

    @GetMapping("/saludo")
    public String saludar() {
        return "Hola, Spring Boot está funcionando correctamente";
    }

    @GetMapping("/info")
    public ProyectoInfo obtenerInfo() {
        return new ProyectoInfo(
            "Desarrollo Web Integrado",
            "Semana 1",
            "Spring Boot + Maven + Java 25",
            "Entorno configurado correctamente"
        );
    }

    @GetMapping("/version")
    public String version() {
        return "Aplicación Semana 1 - Versión 1.0.0";
    }

    @GetMapping("/estado")
    public EstadoInfo estado() {
        return new EstadoInfo("semana1", true, "Backend disponible");
    }
}
