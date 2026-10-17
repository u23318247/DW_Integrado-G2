package com.utp.tienda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.security.CustomUserDetailsService;
import com.utp.tienda.security.TokenService;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tarea 7 - Pruebas del slice del controlador de autenticacion.
 *
 * <p>Se simulan el {@link AuthenticationManager} (validacion contra MySQL) y el
 * {@link TokenService} (emision del JWT) para aislar el controlador.</p>
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String JSON_LOGIN = """
            {"username": "admin", "password": "Admin123*"}
            """;

    private Authentication crearAutenticacionAdmin() {
        return UsernamePasswordAuthenticationToken.authenticated(
                "admin", "Admin123*",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Jwt jwtDeAdmin() {
        return Jwt.withTokenValue("token-falso-de-prueba")
                .header("alg", "HS256")
                .subject("admin")
                .claim("roles", List.of("ROLE_ADMIN", "ROLE_USER"))
                .build();
    }

    @Test
    @DisplayName("1) POST /api/auth/login sin credenciales es publico y devuelve 401 si fallan")
    void loginConCredencialesInvalidasDevuelve401() throws Exception {
        given(authenticationManager.authenticate(any(Authentication.class)))
                .willThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("2) POST /api/auth/login valido devuelve 200 con token Bearer y expiracion")
    void loginExitosoDevuelve200() throws Exception {
        given(authenticationManager.authenticate(any(Authentication.class)))
                .willReturn(crearAutenticacionAdmin());
        given(tokenService.crearToken(any(Authentication.class)))
                .willReturn("jwt.firma.fake");
        given(tokenService.expiresInSeconds()).willReturn(1800L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt.firma.fake"))
                .andExpect(jsonPath("$.expiresInSeconds").value(1800));
    }

    @Test
    @DisplayName("3) POST /api/auth/login con body invalido devuelve 400")
    void loginConBodyInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\", \"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4) GET /api/auth/me exige token: sin token responde 401")
    void meSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", Matchers.containsString("Bearer")));
    }

    @Test
    @DisplayName("5) GET /api/auth/me con token devuelve usuario y roles")
    void meConTokenDevuelvePerfil() throws Exception {
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwtDeAdmin());

        mockMvc.perform(get("/api/auth/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").value(Matchers.hasItems("ROLE_ADMIN", "ROLE_USER")));
    }
}