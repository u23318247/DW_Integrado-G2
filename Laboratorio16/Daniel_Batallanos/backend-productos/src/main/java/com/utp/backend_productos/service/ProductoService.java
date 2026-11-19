package com.utp.backend_productos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.utp.backend_productos.dto.ProductoRequest;
import com.utp.backend_productos.exception.RecursoNoEncontradoException;
import com.utp.backend_productos.model.Producto;

@Service
public class ProductoService {
    private final List<Producto> productos = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(3);

    public ProductoService() {
        productos.add(new Producto(1L, "Laptop Lenovo", 3500.00, 8, LocalDate.now()));
        productos.add(new Producto(2L, "Mouse Logitech", 80.00, 25, LocalDate.now()));
    }

    public List<Producto> listar() {
        return productos;
    }

    public Producto obtenerPorId(Long id) {
        return productos.stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto(
                secuencia.getAndIncrement(),
                request.getNombre(),
                request.getPrecio(),
                request.getStock(),
                request.getFechaRegistro());
        productos.add(producto);
        return producto;
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerPorId(id);
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setFechaRegistro(request.getFechaRegistro());
        return producto;
    }

    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productos.remove(producto);
    }
}
