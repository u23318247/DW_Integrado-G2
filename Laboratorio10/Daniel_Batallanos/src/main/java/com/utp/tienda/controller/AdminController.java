package com.utp.tienda.controller;

import com.utp.tienda.dto.ResumenBackendResponse;
import com.utp.tienda.service.AdminResumenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminResumenService service;

    public AdminController(AdminResumenService service) {
        this.service = service;
    }

    @GetMapping("/resumen")
    public ResumenBackendResponse resumen() {
        return service.obtenerResumen();
    }
}
