package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.model.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long> {
    Optional<Reclamacion> findByCodigoReclamo(String codigoReclamo);
    long countByCodigoReclamoStartingWith(String prefix);
}
