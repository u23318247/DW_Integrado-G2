package com.utp.tienda.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.tienda.model.Producto;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.ProductoRepository;
import com.utp.tienda.repository.UsuarioRepository;
import com.utp.tienda.service.ProductoService;

/**
 * Matriz de pruebas de la guia (seccion 11) con el contexto completo:
 * usuarios reales creados por DatosSeguridadIniciales, BCrypt, login y Bearer Token.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtIntegracionTest {

    private static final String PRODUCTO_JSON = """
            {"nombre":"Audifonos USB","categoria":"Tecnologia","precio":190.00,"stock":7}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    // ---------- Matriz de la guia ----------

    @Test
    void m01_estadoPublicoSinToken_200() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("API disponible"));
    }

    @Test
    void m02_loginValido_200ConJwt() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"usuario","password":"Usuario123*"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresInSeconds").value(1800))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void m03_loginContrasenaErronea_401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void m04_productosSinToken_401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void m05_productosConTokenUser_200() throws Exception {
        mockMvc.perform(get("/api/productos").header("Authorization", bearer("usuario", "Usuario123*")))
                .andExpect(status().isOk());
    }

    @Test
    void m06_crearConTokenUser_403() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .header("Authorization", bearer("usuario", "Usuario123*"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void m07_crearConTokenAdmin_201() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .header("Authorization", bearer("admin", "Admin123*"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Audifonos USB"));
    }

    @Test
    void m08_meConTokenAdmin_200ConRoles() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer("admin", "Admin123*")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_ADMIN")))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_USER")));
    }

    @Test
    void m09_tokenConFirmaModificada_401() throws Exception {
        String token = login("usuario", "Usuario123*");
        String[] partes = token.split("\\.");
        char ultimo = partes[2].charAt(partes[2].length() - 2);
        String firmaAlterada = partes[2].substring(0, partes[2].length() - 2)
                + (ultimo == 'A' ? 'B' : 'A')
                + partes[2].charAt(partes[2].length() - 1);
        String alterado = partes[0] + "." + partes[1] + "." + firmaAlterada;

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void m10_tokenVencido_401() throws Exception {
        // Firmado con la clave correcta, pero vencido hace 10 minutos (supera la tolerancia de 60 s).
        Instant hace20 = Instant.now().minus(20, ChronoUnit.MINUTES);
        String vencido = firmar(JwtClaimsSet.builder()
                .issuer("tienda-api")
                .subject("admin")
                .issuedAt(hace20)
                .expiresAt(hace20.plus(10, ChronoUnit.MINUTES))
                .claim("roles", List.of("ROLE_ADMIN"))
                .build());

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + vencido))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Casos adicionales ----------

    @Test
    void tokenConEmisorDistinto_401() throws Exception {
        Instant ahora = Instant.now();
        String otroEmisor = firmar(JwtClaimsSet.builder()
                .issuer("otra-api")
                .subject("admin")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(10, ChronoUnit.MINUTES))
                .claim("roles", List.of("ROLE_ADMIN"))
                .build());

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + otroEmisor))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCamposVacios_400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void elJwtNoContieneLaContrasena() throws Exception {
        String token = login("admin", "Admin123*");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));

        assertThat(payload).contains("\"sub\":\"admin\"", "\"iss\":\"tienda-api\"", "ROLE_ADMIN")
                .doesNotContain("Admin123*", "$2a$");
    }

    @Test
    void contrasenasSiguenGuardadasConBCrypt() {
        Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();

        assertThat(admin.getPassword()).startsWith("$2a$");
    }

    // ---------- Reto de aplicacion: /api/admin/reporte ----------

    @Test
    void reporteSinToken_401() throws Exception {
        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reporteConTokenUser_403() throws Exception {
        mockMvc.perform(get("/api/admin/reporte").header("Authorization", bearer("usuario", "Usuario123*")))
                .andExpect(status().isForbidden());
    }

    @Test
    void reporteConTokenAdmin_200() throws Exception {
        mockMvc.perform(get("/api/admin/reporte").header("Authorization", bearer("admin", "Admin123*")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaServidor").isNotEmpty())
                .andExpect(jsonPath("$.totalProductos").isNumber());
    }

    // ---------- @PreAuthorize de la semana 8 sigue activo ----------

    @Test
    @WithMockUser(roles = "USER")
    void preAuthorizeSigueBloqueandoEliminarSinRolAdmin() {
        Long id = productoRepository.save(
                new Producto("Mouse", "Tecnologia", new BigDecimal("80.00"), 3)).getId();

        assertThatThrownBy(() -> productoService.eliminar(id))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    // ---------- Utilidades ----------

    private String login(String username, String password) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(respuesta);
        return json.get("accessToken").asText();
    }

    private String bearer(String username, String password) throws Exception {
        return "Bearer " + login(username, password);
    }

    private String firmar(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
