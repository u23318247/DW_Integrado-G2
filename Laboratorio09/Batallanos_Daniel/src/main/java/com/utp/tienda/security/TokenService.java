package com.utp.tienda.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

/**
 * Tarea 5 - Emision de JSON Web Tokens firmados con HS256.
 *
 * <p>El token generado es compacto y autocontenido; el Resource Server lo verifica
 * en cada peticion sin consultar la base de datos. Claims emitidos:</p>
 * <ul>
 *   <li>{@code sub}: nombre de usuario ({@code Authentication#getName()})</li>
 *   <li>{@code iss}: {@code app.jwt.issuer} (tienda-api)</li>
 *   <li>{@code iat}: instante de emision</li>
 *   <li>{@code exp}: instante de expiracion (iat + TTL)</li>
 *   <li>{@code roles}: lista de autoridades con prefijo ROLE_</li>
 * </ul>
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private static final String PREFIJO_ROL = "ROLE_";

    private final JwtEncoder encoder;
    private final String issuer;
    private final long ttlMinutes;

    public TokenService(JwtEncoder encoder,
                        @Value("${app.jwt.issuer}") String issuer,
                        @Value("${app.jwt.ttl-minutes:30}") long ttlMinutes) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    /** Vigencia del token en segundos (TTL en minutos x 60). */
    public long expiresInSeconds() {
        return this.ttlMinutes * 60;
    }

    /**
     * Crea y firma un JWT con los roles del usuario autenticado.
     *
     * @param autenticacion autenticacion exitosa devuelta por el AuthenticationManager
     * @return token firmado en formato compacto (string)
     */
    public String crearToken(Authentication autenticacion) {

        List<String> roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(PREFIJO_ROL))
                .sorted()
                .toList();

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(this.issuer)
                .subject(autenticacion.getName())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(this.ttlMinutes, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = this.encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        log.debug("Token emitido para {} con roles {}", autenticacion.getName(), roles);
        return token;
    }
}