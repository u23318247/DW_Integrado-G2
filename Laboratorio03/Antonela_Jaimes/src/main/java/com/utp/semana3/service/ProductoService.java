package com.utp.semana3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.utp.semana3.model.Producto;

@Service
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private long secuencia = 1;

    public Producto registrar(Producto producto) {
        validar(producto);
        producto.setId(secuencia++);
        productos.add(producto);
        return producto;
    }

    public List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean eliminar(Long id) {
        return productos.removeIf(p -> p.getId().equals(id));
    }

    public Optional<Producto> actualizar(Long id, Producto datosActualizados) {
        validar(datosActualizados);
        Optional<Producto> optProducto = buscarPorId(id);
        if (optProducto.isPresent()) {
            Producto p = optProducto.get();
            p.setNombre(datosActualizados.getNombre());
            p.setPrecio(datosActualizados.getPrecio());
            p.setStock(datosActualizados.getStock());
            return Optional.of(p);
        }
        return Optional.empty();
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
