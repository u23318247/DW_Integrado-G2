package com.utp.productosapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.productosapi.model.Producto;

/**
 * Spring Data JPA implementa esta interfaz en tiempo de ejecucion.
 * Hereda save(), findAll(), findById(), existsById(), deleteById(), etc.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
