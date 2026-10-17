package com.proyectomdweb.proyectomdweb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/auth/login";
        }
        boolean tieneAccesoPanel = auth.getAuthorities().stream()
                .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN") 
                              || rol.getAuthority().equals("ROLE_VENDEDOR")
                              || rol.getAuthority().equals("ROLE_ALMACENERO")
                              || rol.getAuthority().equals("ROLE_REPARTIDOR"));
        if (tieneAccesoPanel) {
            return "redirect:/admin/dashboard";
        }
        return "index";
    }
}
