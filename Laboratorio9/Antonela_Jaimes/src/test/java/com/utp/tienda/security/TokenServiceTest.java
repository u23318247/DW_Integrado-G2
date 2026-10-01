package com.utp.tienda.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private final String secretBase64 = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final String issuer = "tienda-api";
    private final long ttlMinutes = 30;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(issuer, ttlMinutes, secretBase64);
    }

    @Test
    @DisplayName("Debe generar un token JWT valido y extraer el subject")
    void testGenerarYValidarToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = tokenService.generarToken(auth);

        assertNotNull(token);
        assertTrue(tokenService.validarToken(token));
        assertEquals("admin", tokenService.extraerUsername(token));

        List<String> roles = tokenService.extraerRoles(token);
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Debe generar y validar token con claims adicionales")
    void testGenerarTokenConClaims() {
        Map<String, Object> extra = Map.of("email", "admin@tienda.com", "departamento", "IT");
        String token = tokenService.generarTokenConClaims("admin", List.of("ROLE_ADMIN"), extra);

        assertTrue(tokenService.validarToken(token));
        Claims claims = tokenService.extraerClaims(token);
        assertEquals("admin", claims.getSubject());
        assertEquals("admin@tienda.com", claims.get("email"));
        assertEquals("IT", claims.get("departamento"));
    }

    @Test
    @DisplayName("Debe rechazar un token manipulado o con firma invalida")
    void testTokenInvalido() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWNrZXIifQ.invalidsignature";
        assertFalse(tokenService.validarToken(tokenInvalido));
    }
}
