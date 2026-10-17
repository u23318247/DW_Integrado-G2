package com.utp.tienda.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.utp.tienda.model.Rol;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.UsuarioRepository;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Tarea 4 - Pruebas unitarias del {@link CustomUserDetailsService}.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(usuarioRepository);
    }

    private static Rol rol(String nombre) {
        return new Rol(nombre);
    }

    private static Usuario usuario(String username, String hash, boolean activo, Set<Rol> roles) {
        Usuario usuario = new Usuario(username, hash, activo);
        roles.forEach(usuario::agregarRol);
        return usuario;
    }

    @Test
    @DisplayName("1) Lanza UsernameNotFoundException si el usuario no existe")
    void usuarioNoEncontradoLanzaExcepcion() {
        given(usuarioRepository.findByUsername("desconocido")).willReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userDetailsService.loadUserByUsername("desconocido"))
                .withMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("2) Mapea los roles de la entidad a autoridades con prefijo ROLE_")
    void mapeaRolesAAutoridades() {
        Set<Rol> roles = new LinkedHashSet<>(Set.of(rol("ROLE_USER"), rol("ROLE_ADMIN")));
        Usuario usuario = usuario("admin", "$2a$10$hashadmin", true, roles);
        given(usuarioRepository.findByUsername("admin")).willReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$hashadmin");
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("3) Un usuario inactivo se carga deshabilitado (disabled = true)")
    void usuarioInactivoQuedaDeshabilitado() {
        Usuario usuario = usuario("suspendido", "$2a$10$hash", false, Set.of(rol("ROLE_USER")));
        given(usuarioRepository.findByUsername("suspendido")).willReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("suspendido");

        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("4) Conserva el hash BCrypt almacenado (nunca la contrasena en claro)")
    void conservaHashBcrypt() {
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Usuario usuario = usuario("usuario", hash, true, Set.of(rol("ROLE_USER")));
        given(usuarioRepository.findByUsername("usuario")).willReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario");

        assertThat(userDetails.getPassword()).isEqualTo(hash).startsWith("$2a$");
    }
}