package com.proyectomdweb.proyectomdweb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Authentication auth) {
        // La tienda virtual está abierta para todos los visitantes sin forzar login previo.
        return "index";
    }
}
