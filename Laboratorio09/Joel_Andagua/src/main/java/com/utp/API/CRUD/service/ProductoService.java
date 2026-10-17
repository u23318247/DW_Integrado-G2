package com.utp.API.CRUD.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.API.CRUD.exception.RecursoNoEncontradoException;
import com.utp.API.CRUD.exception.ReglaNegocioException;
import com.utp.API.CRUD.model.MovimientoStock;
import com.utp.API.CRUD.model.Producto;
import com.utp.API.CRUD.repository.MovimientoStockRepository;
import com.utp.API.CRUD.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository, MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El texto de búsqueda no puede estar vacío");
        }
        return productoRepository.buscarPorNombre(texto.trim());
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorRango(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new ReglaNegocioException("El precio mínimo no puede superar al máximo");
        }
        return productoRepository.buscarPorRangoPrecio(min, max);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarStockBajo(Integer limite) {
        return productoRepository.buscarConStockBajo(limite);
    }

    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {
        Producto producto = obtenerProducto(productoId);
        validarSalida(producto, cantidad);

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock(
            producto, "SALIDA", cantidad, LocalDateTime.now()
        );
        movimientoRepository.save(movimiento);

        return producto;
    }

    @Transactional
    public void simularSalidaConError(Long productoId, int cantidad) {
        Producto producto = obtenerProducto(productoId);
        validarSalida(producto, cantidad);

        producto.setStock(producto.getStock() - cantidad);
        movimientoRepository.save(new MovimientoStock(
            producto, "SALIDA", cantidad, LocalDateTime.now()
        ));

        throw new IllegalStateException("Error simulado: la transacción debe hacer rollback");
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    private void validarSalida(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        if (producto.getStock() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente. Disponible: " + producto.getStock());
        }
    }
}