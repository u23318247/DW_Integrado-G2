package com.utp.tienda.controller;

import com.utp.tienda.dto.LoginRequest;
import com.utp.tienda.dto.TokenResponse;
import com.utp.tienda.security.TokenService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarea 7 - Autenticacion y emision de tokens JWT.
 *
 * <p>Flujo:</p>
 * <ol>
 *   <li>{@code POST /api/auth/login} (publico) valida usuario + contrasena con el
 *       {@link AuthenticationManager}, que consulta MySQL y verifica el hash BCrypt.</li>
 *   <li>Si la autenticacion es correcta se emite un JWT firmado con HS256 y se
 *       devuelve como {@code Bearer}.</li>
 *   <li>El cliente lo envia en {@code Authorization: Bearer &lt;token&gt;}.</li>
 *   <li>{@code GET /api/auth/me} devuelve el perfil del portador del token.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String PREFIJO_ROL = "ROLE_";

    private final AuthenticationManager manager;
    private final TokenService tokens;

    public AuthController(AuthenticationManager manager, TokenService tokens) {
        this.manager = manager;
        this.tokens = tokens;
    }

    /**
     * Autentica al usuario y devuelve el token JWT.
     *
     * <p>200 OK con el token, 401 si las credenciales son invalidas y 400 si el
     * cuerpo no cumple las validaciones de {@link LoginRequest}.</p>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        try {
            Authentication auth = this.manager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(), request.password()));

            String token = this.tokens.crearToken(auth);

            return ResponseEntity.ok(new TokenResponse(
                    "Bearer", token, this.tokens.expiresInSeconds()));

        } catch (AuthenticationException ex) {
            // No se revela si el usuario existe o no: mismo error en ambos casos
            log.warn("Fallo de autenticacion para '{}': {}", request.username(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    /**
     * Perfil del usuario autenticado, leido del token Bearer.
     * Responde 401 si el token falta, expiro o tiene la firma alterada.
     */
    @GetMapping("/me")
    public Map<String, Object> me(JwtAuthenticationToken auth) {

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(PREFIJO_ROL))
                .sorted()
                .toList();

        return Map.of(
                "username", auth.getName(),
                "roles", roles);
    }
}