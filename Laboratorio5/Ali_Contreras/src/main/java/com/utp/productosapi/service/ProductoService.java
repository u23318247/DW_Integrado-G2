package com.utp.productosapi.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.utp.productosapi.model.Producto;

/**
 * Contiene la logica de negocio y las validaciones.
 * Almacenamiento temporal en memoria: ConcurrentHashMap + AtomicLong.
 * En la semana 6 sera reemplazado por Spring Data JPA + MySQL.
 */
@Service
public class ProductoService {

    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>(productos.values());
        lista.sort(Comparator.comparing(Producto::getId));
        return lista;
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

    /**
     * Reto integrador: devuelve los productos cuyo nombre contiene el texto,
     * sin diferenciar mayusculas de minusculas.
     */
    public List<Producto> buscarPorNombre(String nombre) {
        String criterio = nombre == null ? "" : nombre.trim().toLowerCase();
        return productos.values()
                .stream()
                .filter(producto -> producto.getNombre().toLowerCase().contains(criterio))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
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
