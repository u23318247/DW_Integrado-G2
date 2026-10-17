package com.utp.tienda.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.tienda.model.Producto;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.ProductoRepository;
import com.utp.tienda.repository.UsuarioRepository;
import com.utp.tienda.service.ProductoService;

/**
 * Prueba de integracion con el contexto completo: usuarios reales creados por
 * DatosSeguridadIniciales, contrasenas BCrypt y autenticacion HTTP Basic.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductoService productoService;

    @Test
    void contrasenasSeGuardanComoHashBCrypt() {
        Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();

        assertThat(admin.getPassword()).startsWith("$2a$").isNotEqualTo("Admin123*");
        assertThat(passwordEncoder.matches("Admin123*", admin.getPassword())).isTrue();
    }

    @Test
    void usuarioRealConRoleUserPuedeListar() throws Exception {
        mockMvc.perform(get("/api/productos").with(httpBasic("usuario", "Usuario123*")))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioRealConRoleUserNoPuedeCrear() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .with(httpBasic("usuario", "Usuario123*"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","categoria":"Tecnologia","precio":150.00,"stock":5}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRealPuedeCrear() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .with(httpBasic("admin", "Admin123*"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","categoria":"Tecnologia","precio":150.00,"stock":5}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void contrasenaIncorrectaResponde401() throws Exception {
        mockMvc.perform(get("/api/productos").with(httpBasic("admin", "incorrecta")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioInexistenteResponde401() throws Exception {
        mockMvc.perform(get("/api/productos").with(httpBasic("intruso", "x")))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Reto avanzado: @PreAuthorize en el Service ----------

    @Test
    @WithMockUser(roles = "USER")
    void preAuthorizeBloqueaEliminarDesdeServiceSinRolAdmin() {
        Long id = productoRepository.save(
                new Producto("Mouse", "Tecnologia", new BigDecimal("80.00"), 3)).getId();

        assertThatThrownBy(() -> productoService.eliminar(id))
                .isInstanceOf(AuthorizationDeniedException.class);
        assertThat(productoRepository.existsById(id)).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void preAuthorizePermiteEliminarDesdeServiceConRolAdmin() {
        Long id = productoRepository.save(
                new Producto("Mouse", "Tecnologia", new BigDecimal("80.00"), 3)).getId();

        productoService.eliminar(id);

        assertThat(productoRepository.existsById(id)).isFalse();
    }
}
