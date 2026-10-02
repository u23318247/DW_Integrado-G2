package com.utp.tienda.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.security.CustomUserDetailsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tarea 7.1 - Pruebas del endpoint publico bajo la arquitectura JWT.
 *
 * <p>Criterio de aceptacion: {@code GET /api/publico/estado} responde 200 OK
 * <b>sin</b> token y sin reto {@code WWW-Authenticate}.</p>
 */
@WebMvcTest(PublicoController.class)
@Import(SecurityConfig.class)
class PublicoControllerSecurityTest {

    private static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtToken =
            SecurityMockMvcRequestPostProcessors.jwt();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("1) GET /api/publico/estado sin credenciales responde 200 OK")
    void estadoSinAutenticacionDevuelve200() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("API disponible"))
                // Al ser publico no debe retar al cliente con WWW-Authenticate
                .andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    @Test
    @DisplayName("2) GET /api/publico/estado con un token valido tambien responde 200 OK")
    void estadoConAutenticacionDevuelve200() throws Exception {
        mockMvc.perform(get("/api/publico/estado")
                        .with(jwtToken.jwt(jwt -> jwt.subject("admin"))))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("API disponible")));
    }

    @Test
    @DisplayName("3) Un endpoint no publicado exige token y responde 401 con esquema Bearer")
    void endpointNoPublicadoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/otro/recurso"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", Matchers.containsString("Bearer")));
    }

    @Test
    @DisplayName("4) HTTP Basic esta desactivado: un header Basic no autentica")
    void headerBasicNoAutentica() throws Exception {
        mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Basic YWRtaW46QWRtaW4xMjMq"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", Matchers.containsString("Bearer")))
                .andExpect(header().string("WWW-Authenticate", Matchers.not(Matchers.containsString("Basic"))));
    }

    @Test
    @DisplayName("5) Un metodo no soportado en la ruta publica no expone datos: 405")
    void metodoNoSoportadoEnRutaPublicaDevuelve405() throws Exception {
        mockMvc.perform(post("/api/publico/estado"))
                .andExpect(status().isMethodNotAllowed());
    }
}