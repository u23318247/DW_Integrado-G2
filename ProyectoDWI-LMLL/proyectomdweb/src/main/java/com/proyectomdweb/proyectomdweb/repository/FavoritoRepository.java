package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioClaveOrderByFechaAgregadoDesc(String usuarioClave);
    Optional<Favorito> findByUsuarioClaveAndProductoId(String usuarioClave, Long productoId);
}
