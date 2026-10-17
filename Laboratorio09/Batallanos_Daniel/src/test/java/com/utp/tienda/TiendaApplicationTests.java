package com.utp.tienda;

import static org.assertj.core.api.Assertions.assertThat;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.repository.RolRepository;
import com.utp.tienda.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Verifica que el contexto de aplicacion arranca correctamente: la cadena de
 * filtros de Spring Security, los repositorios y la semilla de datos de seguridad.
 */
@SpringBootTest
class TiendaApplicationTests {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Test
    @DisplayName("El contexto carga con la cadena de filtros y la semilla de datos")
    void contextoCargaCorrectamente() {
        assertThat(securityFilterChain).isNotNull();
        assertThat(rolRepository.findByNombre("ROLE_USER")).isPresent();
        assertThat(rolRepository.findByNombre("ROLE_ADMIN")).isPresent();
        assertThat(usuarioRepository.existsByUsername("usuario")).isTrue();
        assertThat(usuarioRepository.existsByUsername("admin")).isTrue();
    }
}