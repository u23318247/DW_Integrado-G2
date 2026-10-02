package com.utp.tienda.repository;

import com.utp.tienda.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la tabla {@code usuarios}.
 *
 * <p>{@code findByUsername} es el metodo que consume el
 * {@code CustomUserDetailsService} durante el proceso de autenticacion HTTP Basic.</p>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}