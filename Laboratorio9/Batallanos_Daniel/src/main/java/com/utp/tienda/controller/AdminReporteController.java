package com.utp.tienda.controller;

import com.utp.tienda.repository.ProductoRepository;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarea 8 - Reto de consolidacion: reporte administrativo.
 *
 * <p>Queda protegido automaticamente por la regla
 * {@code GET /api/admin/** -> hasRole("ADMIN")} del {@code SecurityFilterChain}:
 * un token de {@code ROLE_USER} recibe 403 Forbidden.</p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminReporteController {

    private final ProductoRepository productoRepository;

    public AdminReporteController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** GET /api/admin/reporte -> 200 OK solo con ROLE_ADMIN, 403 para ROLE_USER. */
    @GetMapping("/reporte")
    public Map<String, Object> reporte() {
        return Map.of(
                "fecha", LocalDate.now().toString(),
                "totalProductos", this.productoRepository.count());
    }
}