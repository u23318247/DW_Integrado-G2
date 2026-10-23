package com.utp.tienda.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.utp.tienda.exception.ProductoNoEncontradoException;
import com.utp.tienda.exception.StockInsuficienteException;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;

@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public InventarioService(ProductoRepository productoRepository,
                             MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public Producto registrarSalida(Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException(productoId));

        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(
                "Stock actual: " + producto.getStock() + ", solicitado: " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad);

        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProducto(producto);
        movimiento.setTipo("SALIDA");
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        return producto;
    }
}
