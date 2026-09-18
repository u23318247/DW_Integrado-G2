package com.utp.productosapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.repository.ProductoRepository;

/**
 * Semana 6: el Service ya no administra una coleccion en memoria.
 * Recibe ProductoRepository por inyeccion de constructor y le delega la persistencia.
 * La frontera transaccional se declara aqui para hacer explicita la unidad de trabajo.
 */
@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Producto crear(Producto producto) {
        validar(producto);
        producto.setId(null);
        return repository.save(producto);
    }

    @Transactional
    public Optional<Producto> actualizar(Long id, Producto datos) {
        validar(datos);
        return repository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setCategoria(datos.getCategoria());
            existente.setPrecio(datos.getPrecio());
            existente.setStock(datos.getStock());
            return repository.save(existente);
        });
    }

    @Transactional
    public Optional<Producto> actualizarPrecio(Long id, double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        return repository.findById(id).map(existente -> {
            existente.setPrecio(precio);
            return repository.save(existente);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private void validar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoria es obligatoria");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}
