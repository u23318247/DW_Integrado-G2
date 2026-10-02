package com.utp.tienda.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.tienda.repository.ProductoRepository;

/**
 * Reto de aplicacion (semana 9): reporte administrativo.
 * Controller -> Service -> Repository, igual que el resto de la API.
 */
@Service
public class ReporteService {

    private final ProductoRepository productoRepository;

    public ReporteService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generarReporte() {
        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("fechaServidor", LocalDateTime.now().toString());
        reporte.put("totalProductos", productoRepository.count());
        return reporte;
    }
}
