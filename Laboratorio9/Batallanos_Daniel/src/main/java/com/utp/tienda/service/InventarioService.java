package com.utp.tienda.service;

import com.utp.tienda.dto.AjusteInventarioRequest;
import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.exception.ReglaNegocioException;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.model.TipoMovimiento;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de inventario (modulo de consolidacion, Tarea 9.1).
 *
 * <p>Los ajustes de stock son la operacion mas sensible del modulo: solo el rol
 * ADMIN puede ejecutarlos, y cada movimiento queda auditado con el usuario
 * autenticado que lo realizo.</p>
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoStockRepository;

    public InventarioService(ProductoRepository productoRepository,
                             MovimientoStockRepository movimientoStockRepository) {
        this.productoRepository = productoRepository;
        this.movimientoStockRepository = movimientoStockRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> listarMovimientos() {
        return movimientoStockRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> listarMovimientosPorProducto(Long productoId) {
        return movimientoStockRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    @Transactional(readOnly = true)
    public Producto consultarStock(Long productoId) {
        return productoRepository.findByIdAndActivoTrue(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + productoId));
    }

    /**
     * Registra un ajuste de inventario. Protegido a nivel de metodo
     * ({@code hasRole('ADMIN')}) y a nivel de URL
     * ({@code POST /api/inventario/ajustes} -> hasRole("ADMIN")).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovimientoStock registrarAjuste(AjusteInventarioRequest request) {

        Producto producto = productoRepository.findByIdAndActivoTrue(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + request.productoId()));

        TipoMovimiento tipo = TipoMovimiento.valueOf(request.tipo().toUpperCase());

        if (tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.AJUSTE_NEGATIVO) {
            if (producto.getStock() < request.cantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para el producto "
                        + producto.getId() + ". Disponible: " + producto.getStock());
            }
            producto.descontarStock(request.cantidad());
        } else {
            producto.agregarStock(request.cantidad());
        }
        productoRepository.save(producto);

        MovimientoStock movimiento = new MovimientoStock(
                producto, tipo, request.cantidad(), request.motivo(), usuarioActual());
        return movimientoStockRepository.save(movimiento);
    }

    /** Nombre del usuario autenticado que ejecuta la operacion (trazabilidad). */
    private String usuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "sistema";
    }
}