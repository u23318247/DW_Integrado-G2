package com.utp.tienda.security;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.model.Producto;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.ProductoRepository;
import com.utp.tienda.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Pruebas de integracion end-to-end de la arquitectura JWT (stateless).
 *
 * <p>Levanta el contexto completo con H2 en memoria y valida los criterios de
 * aceptacion: emision de token en {@code POST /api/auth/login}, uso de
 * {@code Authorization: Bearer <token>}, autorizacion por el claim
 * {@code roles}, rechazo de tokens manipulados o vencidos, ausencia de
 * HTTP Basic y ausencia de sesiones (JSESSIONID).</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    private static final String CLAVE_USER = "Usuario123*";
    private static final String CLAVE_ADMIN = "Admin123*";

    // -------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------

    /** Autentica contra el endpoint real de login y devuelve el accessToken. */
    private String login(String username, String password) throws Exception {
        String cuerpo = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}"
                                .formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return (String) parse(cuerpo).read("$.accessToken");
    }

    private Producto crearProducto(String nombre, int stock) {
        return productoRepository.save(
                new Producto(nombre, new BigDecimal("99.90"), stock, "Producto de prueba"));
    }

    private static String jsonProducto(String nombre) {
        return """
                {
                  "nombre": "%s",
                  "precio": 129.90,
                  "stock": 20,
                  "descripcion": "Producto creado desde la prueba de integracion"
                }
                """.formatted(nombre);
    }

    /** Token con firma manipulada: se altera el segmento de firma. */
    private String tokenAlterado(String tokenValido) {
        int ultimos = 6;
        return tokenValido.substring(0, tokenValido.length() - ultimos)
                + "AAAAAA";
    }

    /** Token correctamente firmado pero ya expirado. */
    private String tokenVencido(String username, List<String> roles) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tienda-api")
                .subject(username)
                .issuedAt(ahora.minus(2, ChronoUnit.HOURS))
                .expiresAt(ahora.minus(1, ChronoUnit.HOURS))
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    // -------------------------------------------------------------
    // Endpoint publico
    // -------------------------------------------------------------
    @Test
    @DisplayName("1) GET /api/publico/estado responde 200 OK sin token")
    void estadoPublicoSinToken() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("API disponible"))
                .andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    // -------------------------------------------------------------
    // Emision y rechazo de tokens
    // -------------------------------------------------------------
    @Test
    @DisplayName("2) POST /api/auth/login con credenciales validas devuelve 200 y un Bearer token")
    void loginExitosoDevuelveToken() throws Exception {
        String cuerpo = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"" + CLAVE_ADMIN + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresInSeconds").value(1800))
                .andReturn().getResponse().getContentAsString();

        String token = (String) parse(cuerpo).read("$.accessToken");
        // Formato compacto de JWT: header.payload.firma
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("3) POST /api/auth/login con contrasena erronea devuelve 401")
    void loginConPasswordErroneaDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"claveIncorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("4) POST /api/auth/login con usuario inexistente devuelve 401")
    void loginConUsuarioInexistenteDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"noExiste\",\"password\":\"" + CLAVE_USER + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("5) POST /api/auth/login sin cuerpo valido devuelve 400")
    void loginConBodyInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erroresCampos.username").exists())
                .andExpect(jsonPath("$.erroresCampos.password").exists());
    }

    // -------------------------------------------------------------
    // Sin dependencias de sesion y HTTP Basic desactivado
    // -------------------------------------------------------------
    @Test
    @DisplayName("6) Sin token, las rutas protegidas devuelven 401 con esquema Bearer (no Basic)")
    void sinTokenDevuelve401Bearer() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("Bearer")))
                .andExpect(header().string("WWW-Authenticate", not(containsString("Basic"))));
    }

    @Test
    @DisplayName("7) HTTP Basic esta desactivado: un header Basic no autentica")
    void httpBasicDesactivado() throws Exception {
        mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Basic YWRtaW46QWRtaW4xMjMq"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", not(containsString("Basic"))));
    }

    @Test
    @DisplayName("8) La API es stateless: ninguna peticion emite cookie JSESSIONID")
    void ningunaPeticionEmiteCookieDeSesion() throws Exception {
        String token = login("admin", CLAVE_ADMIN);

        MvcResult conToken = mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult sinToken = mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(conToken.getResponse().getCookies()).isEmpty();
        assertThat(sinToken.getResponse().getCookies()).isEmpty();
    }

    // -------------------------------------------------------------
    // Validacion de roles por token
    // -------------------------------------------------------------
    @Test
    @DisplayName("9) Token de usuario (ROLE_USER): lee productos pero no escribe ni entra a admin")
    void tokenDeUsuarioRespetaMatrizDeAcceso() throws Exception {
        String token = login("usuario", CLAVE_USER);

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProducto("Producto de usuario")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/reporte").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        Producto producto = crearProducto("Producto protegido", 5);
        mockMvc.perform(delete("/api/productos/" + producto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        assertThat(productoRepository.findById(producto.getId())).isPresent();
    }

    @Test
    @DisplayName("10) Token de admin (ROLE_ADMIN): acceso total a productos y reporte")
    void tokenDeAdminRespetaMatrizDeAcceso() throws Exception {
        String token = login("admin", CLAVE_ADMIN);

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProducto("Monitor 24 pulgadas")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/admin/reporte").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").isNumber())
                .andExpect(jsonPath("$.fecha").exists());

        Producto producto = crearProducto("Producto a eliminar por admin", 5);
        mockMvc.perform(delete("/api/productos/" + producto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(productoRepository.findById(producto.getId())).isEmpty();
    }

    @Test
    @DisplayName("11) GET /api/auth/me devuelve el perfil y los roles del token")
    void perfilDelUsuarioAutenticado() throws Exception {
        String token = login("admin", CLAVE_ADMIN);

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasItems("ROLE_ADMIN")));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------
    // Rechazo de tokens invalidos
    // -------------------------------------------------------------
    @Test
    @DisplayName("12) Un token con la firma alterada se rechaza con 401")
    void tokenConFirmaAlteradaDevuelve401() throws Exception {
        String tokenAlterado = tokenAlterado(login("admin", CLAVE_ADMIN));

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + tokenAlterado))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/reporte").header("Authorization", "Bearer " + tokenAlterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("13) Un token vencido se rechaza con 401")
    void tokenVencidoDevuelve401() throws Exception {
        String vencido = tokenVencido("admin", List.of("ROLE_ADMIN"));

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + vencido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("14) Un token con formato invalido se rechaza con 401")
    void tokenMalformadoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer no-es-un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------
    // Inventario: la regla especifica de ADMIN se conserva
    // -------------------------------------------------------------
    @Test
    @DisplayName("15) Ajustes de inventario: USER 403 y ADMIN 201")
    void ajustesDeInventarioRespetanRoles() throws Exception {
        Producto producto = crearProducto("Producto para ajuste", 10);
        String json = """
                {"productoId": %d, "tipo": "AJUSTE_NEGATIVO",
                 "cantidad": 2, "motivo": "Merma"}
                """.formatted(producto.getId());

        mockMvc.perform(get("/api/inventario/movimientos")
                        .header("Authorization", "Bearer " + login("usuario", CLAVE_USER)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/inventario/ajustes")
                        .header("Authorization", "Bearer " + login("usuario", CLAVE_USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/inventario/ajustes")
                        .header("Authorization", "Bearer " + login("admin", CLAVE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("admin"));
    }

    // -------------------------------------------------------------
    // Conservacion de la semana 8: BCrypt, roles y no exposicion de datos sensibles
    // -------------------------------------------------------------
    @Test
    @DisplayName("16) Las contrasenas siguen almacenadas como hash BCrypt $2a$")
    void contrasenasAlmacenadasComoHashBcrypt() {
        var usuarios = usuarioRepository.findAll();
        assertThat(usuarios).isNotEmpty();

        for (Usuario usuario : usuarios) {
            assertThat(usuario.getPassword())
                    .as("El usuario %s debe tener hash BCrypt", usuario.getUsername())
                    .startsWith("$2")
                    .isNotEqualTo(CLAVE_USER)
                    .isNotEqualTo(CLAVE_ADMIN);
        }

        Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();
        Usuario usuario = usuarioRepository.findByUsername("usuario").orElseThrow();

        assertThat(passwordEncoder.matches(CLAVE_ADMIN, admin.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(CLAVE_USER, usuario.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(CLAVE_ADMIN, usuario.getPassword())).isFalse();
    }

    @Test
    @DisplayName("17) La siembra conserva los roles ROLE_USER y ROLE_ADMIN")
    void siembraDeDatosDeSeguridad() {
        Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();
        Usuario usuario = usuarioRepository.findByUsername("usuario").orElseThrow();

        assertThat(admin.getRoles()).extracting("nombre")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(usuario.getRoles()).extracting("nombre")
                .containsExactly("ROLE_USER");

        assertThat(customUserDetailsService.loadUserByUsername("admin")
                .getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("18) Las respuestas JSON no exponen credenciales")
    void respuestasNoExponenCredenciales() throws Exception {
        String cuerpo = mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Bearer " + login("admin", CLAVE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(cuerpo)
                .doesNotContain("password")
                .doesNotContain("username")
                .doesNotContain("$2a$")
                .doesNotContain("ROLE_");
    }
}