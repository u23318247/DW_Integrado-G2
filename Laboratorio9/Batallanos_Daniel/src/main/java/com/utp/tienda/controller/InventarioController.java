package com.utp.tienda.controller;

import com.utp.tienda.dto.AjusteInventarioRequest;
import com.utp.tienda.dto.MovimientoStockResponse;
import com.utp.tienda.dto.ProductoResponse;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.service.InventarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarea 9.1 - Modulo de inventario (consolidacion).
 *
 * <p>Control de acceso:</p>
 * <ul>
 *   <li>GET /api/inventario/** -&gt; USER o ADMIN (401 sin autenticar)</li>
 *   <li>POST /api/inventario/ajustes -&gt; solo ADMIN (403 para USER)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /** GET /api/inventario/movimientos -&gt; bitacora completa de movimientos. */
    @GetMapping("/movimientos")
    public List<MovimientoStockResponse> listarMovimientos() {
        return inventarioService.listarMovimientos().stream()
                .map(MovimientoStockResponse::from)
                .toList();
    }

    /** GET /api/inventario/movimientos/producto/{id} -&gt; historial de un producto. */
    @GetMapping("/movimientos/producto/{id}")
    public List<MovimientoStockResponse> listarMovimientosPorProducto(@PathVariable Long id) {
        return inventarioService.listarMovimientosPorProducto(id).stream()
                .map(MovimientoStockResponse::from)
                .toList();
    }

    /** GET /api/inventario/stock/{id} -&gt; stock actual del producto. */
    @GetMapping("/stock/{id}")
    public ProductoResponse consultarStock(@PathVariable Long id) {
        return ProductoResponse.from(inventarioService.consultarStock(id));
    }

    /** POST /api/inventario/ajustes -&gt; 201 Created solo para ADMIN. */
    @PostMapping("/ajustes")
    public ResponseEntity<MovimientoStockResponse> registrarAjuste(
            @Valid @RequestBody AjusteInventarioRequest request) {
        MovimientoStock movimiento = inventarioService.registrarAjuste(request);
        return ResponseEntity
                .created(URI.create("/api/inventario/movimientos/" + movimiento.getId()))
                .body(MovimientoStockResponse.from(movimiento));
    }
}