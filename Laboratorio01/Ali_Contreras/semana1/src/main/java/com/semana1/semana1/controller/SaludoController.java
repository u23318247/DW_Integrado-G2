package com.semana1.semana1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semana1.semana1.model.ProyectoInfo;

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
                "Entorno configurado correctamente");
    }
}
