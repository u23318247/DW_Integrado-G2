package com.utp.tienda.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Tarea 9 - Pruebas unitarias de firma y decodificacion del token JWT.
 *
 * <p>El encoder, el decoder y el {@link TokenService} se construyen a mano con una
 * clave HMAC de 32 bytes valida <b>solo para el scope de test</b>, de modo que la
 * prueba es aislada y no depende de Spring ni de la variable de entorno
 * {@code JWT_SECRET_BASE64}.</p>
 */
class TokenServiceTest {

    /** Clave de 32 bytes reservada para pruebas: NUNCA usar en produccion. */
    private static final String CLAVE_TEST_BASE64 =
            Base64.getEncoder().encodeToString(
                    "clave-de-pruebas-jwt-lab09-32bytes".getBytes(StandardCharsets.UTF_8));

    private static final String ISSUER = "tienda-api";
    private static final long TTL_MINUTOS = 30;

    private JwtDecoder decoder;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        byte[] clave = Base64.getDecoder().decode(CLAVE_TEST_BASE64);
        assertThat(clave.length)
                .as("La clave de prueba debe tener al menos 32 bytes")
                .isGreaterThanOrEqualTo(32);

        SecretKey secretKey = new SecretKeySpec(clave, "HmacSHA256");

        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));

        NimbusJwtDecoder nimbusDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        nimbusDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        this.decoder = nimbusDecoder;

        this.tokenService = new TokenService(encoder, ISSUER, TTL_MINUTOS);
    }

    private Authentication crearAutenticacionAdmin() {
        return UsernamePasswordAuthenticationToken.authenticated(
                "admin",
                "Admin123*",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("1) El token firmado se decodifica y conserva los claims esperados")
    void tokenContieneSubjectIssuerRolesYExpiracion() {
        Jwt jwt = this.decoder.decode(this.tokenService.crearToken(crearAutenticacionAdmin()));

        assertThat(jwt.getSubject()).isEqualTo("admin");
        assertThat(jwt.getIssuer().toString()).isEqualTo("tienda-api");
        assertThat(jwt.getClaimAsStringList("roles")).contains("ROLE_ADMIN");
        assertThat(jwt.getExpiresAt()).isNotNull();
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getId()).isNull();
    }

    @Test
    @DisplayName("2) El claim roles incluye todos los roles con prefijo ROLE_")
    void tokenIncluyeTodosLosRoles() {
        Jwt jwt = this.decoder.decode(this.tokenService.crearToken(crearAutenticacionAdmin()));

        assertThat(jwt.getClaimAsStringList("roles"))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("3) El algoritmo de firma del encabezado es HS256")
    void encabezadoUsaHs256() {
        Jwt jwt = this.decoder.decode(this.tokenService.crearToken(crearAutenticacionAdmin()));

        assertThat(jwt.getHeaders().get("alg")).isEqualTo("HS256");
    }

    @Test
    @DisplayName("4) expiresInSeconds devuelve el TTL configurado en minutos x 60")
    void expiracionEnSegundosCorrespondeAlTtl() {
        assertThat(this.tokenService.expiresInSeconds()).isEqualTo(TTL_MINUTOS * 60);
    }

    @Test
    @DisplayName("5) Se excluyen las autoridades que no tienen el prefijo ROLE_")
    void seExcluyenAutoridadesSinPrefijoRol() {
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                "usuario",
                "Usuario123*",
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("SCOPE_perfil")));

        Jwt jwt = this.decoder.decode(this.tokenService.crearToken(auth));

        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ROLE_USER");
    }
}