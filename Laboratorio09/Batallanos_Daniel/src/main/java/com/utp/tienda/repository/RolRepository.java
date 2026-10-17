package com.utp.tienda.repository;

import com.utp.tienda.model.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la tabla {@code roles}.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /** Busca un rol por su nombre exacto (incluye el prefijo ROLE_). */
    Optional<Rol> findByNombre(String nombre);

    /** Verifica si ya existe un rol con ese nombre. */
    boolean existsByNombre(String nombre);
}