package com.utp.tienda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.exception.ReglaNegocioException;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El texto de búsqueda no puede estar vacío ni contener solo espacios");
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
    public List<Producto> buscarConStockBajo(Integer limite) {
        if (limite == null || limite < 0) {
            throw new ReglaNegocioException("El límite de stock debe ser mayor o igual a cero");
        }
        return productoRepository.buscarConStockBajo(limite);
    }

    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {
        Producto producto = obtenerProducto(productoId);
        validarSalida(producto, cantidad);
        producto.setStock(producto.getStock() - cantidad);
        MovimientoStock movimiento = new MovimientoStock(
                producto, "SALIDA", cantidad, LocalDateTime.now());
        movimientoRepository.save(movimiento);
        // El producto se actualiza mediante dirty checking al hacer commit.
        return producto;
    }

    @Transactional
    public Producto registrarEntrada(Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        Producto producto = obtenerProducto(productoId);
        producto.setStock(producto.getStock() + cantidad);
        MovimientoStock movimiento = new MovimientoStock(
                producto, "ENTRADA", cantidad, LocalDateTime.now());
        movimientoRepository.save(movimiento);
        // El producto se actualiza mediante dirty checking al confirmar la transacción.
        return producto;
    }

    @Transactional
    public void simularSalidaConError(Long productoId, int cantidad) {
        Producto producto = obtenerProducto(productoId);
        validarSalida(producto, cantidad);
        producto.setStock(producto.getStock() - cantidad);
        movimientoRepository.save(new MovimientoStock(
                producto, "SALIDA", cantidad, LocalDateTime.now()));
        throw new IllegalStateException(
                "Error simulado: la transacción debe hacer rollback");
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado: " + id));
    }

    private void validarSalida(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        if (producto.getStock() < cantidad) {
            throw new ReglaNegocioException(
                    "Stock insuficiente. Disponible: " + producto.getStock());
        }
    }
}
