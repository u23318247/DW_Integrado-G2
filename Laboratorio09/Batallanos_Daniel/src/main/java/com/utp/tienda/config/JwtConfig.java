package com.utp.tienda.config;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.JWKSource;

/**
 * Tarea 4 - Configuracion criptografica JWT (simetrica HMAC-SHA256 / HS256).
 *
 * <p>La clave viaja en Base64 por la propiedad {@code app.jwt.secret-base64}
 * (variable de entorno {@code JWT_SECRET_BASE64}) y se valida al arrancar: si
 * decodificada mide menos de 32 bytes (256 bits) la aplicacion falla rapido con
 * {@link IllegalArgumentException}, porque una clave mas corta no es aceptable
 * para HMAC-SHA256.</p>
 *
 * <p>La misma {@link SecretKey} se usa para <b>firmar</b> (JwtEncoder) y para
 * <b>verificar</b> (JwtDecoder): al ser un algoritmo simetrico, la clave es la
 * misma en ambos sentidos.</p>
 */
@Configuration
public class JwtConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

    /** Longitud minima de la clave en bytes para HMAC-SHA256 (256 bits). */
    private static final int LONGITUD_MINIMA_BYTES = 32;

    private static final String ALGORITMO = "HmacSHA256";

    /**
     * Decodifica y valida la clave secreta configurada.
     *
     * @param secretBase64 clave en Base64 (property {@code app.jwt.secret-base64})
     * @return {@link SecretKey} utilizable con HMAC-SHA256
     * @throws IllegalArgumentException si la clave esta vacia o mide menos de 32 bytes
     */
    @Bean
    public SecretKey jwtSecretKey(@Value("${app.jwt.secret-base64}") String secretBase64) {

        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta la propiedad app.jwt.secret-base64 (variable de entorno JWT_SECRET_BASE64)");
        }

        byte[] clave;
        try {
            clave = Base64.getDecoder().decode(secretBase64.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "app.jwt.secret-base64 no es un Base64 valido: " + ex.getMessage(), ex);
        }

        if (clave.length < LONGITUD_MINIMA_BYTES) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos " + LONGITUD_MINIMA_BYTES
                            + " bytes (256 bits) para HS256, pero tiene " + clave.length + " bytes");
        }

        log.info("Clave JWT cargada correctamente ({} bytes, {})", clave.length, ALGORITMO);
        return new SecretKeySpec(clave, ALGORITMO);
    }

    /** Codificador: firma los tokens con la clave simetrica usando HS256. */
    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(secretKey);
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * Decodificador: verifica firma, vigencia ({@code exp}) y emisor ({@code iss}).
     * Un token manipulado, vencido o firmado con otra clave se rechaza aqui y el
     * Resource Server responde 401 Unauthorized.
     */
    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey,
                                 @Value("${app.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Valida exp/nbf y que el claim iss coincida con app.jwt.issuer
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

        return decoder;
    }
}