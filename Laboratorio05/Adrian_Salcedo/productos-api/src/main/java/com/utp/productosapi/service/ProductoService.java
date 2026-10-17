package com.utp.productosapi.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.utp.productosapi.model.Producto;

@Service
public class ProductoService {
    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);
    public List<Producto> listar() {
        return new ArrayList<>(productos.values());
    }
    public Optional<Producto> buscarPorId(Long id) {
        return Optional.ofNullable(productos.get(id));
    }
    public Producto crear(Producto producto) {
        validar(producto);
        Long id = secuencia.incrementAndGet();
        producto.setId(id);
        productos.put(id, producto);
        return producto;
    }
    public Optional<Producto> actualizar(Long id, Producto datos) {
        validar(datos);
        Producto existente = productos.get(id);
        if (existente == null) {
            return Optional.empty();
        }
        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        return Optional.of(existente);
    }
    public Optional<Producto> actualizarPrecio(Long id, double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        Producto existente = productos.get(id);
        if (existente == null) {
            return Optional.empty();
        }
        existente.setPrecio(precio);
        return Optional.of(existente);
    }
    public boolean eliminar(Long id) {
        return productos.remove(id) != null;
    }
    private void validar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

}