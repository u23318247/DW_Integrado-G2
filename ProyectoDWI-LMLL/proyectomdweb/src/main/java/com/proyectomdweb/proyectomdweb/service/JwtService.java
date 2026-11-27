package com.proyectomdweb.proyectomdweb.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Llave HMAC-SHA256 leída de la propiedad jwt.secret (Base64, mínimo 256 bits).
    // Al ser fija por ambiente, los tokens siguen siendo válidos tras reiniciar el servidor.
    private final Key secretKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secretBase64,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        // Keys.hmacShaKeyFor lanza WeakKeyException si la llave tiene menos de 256 bits
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expirationMs = expirationMs;
    }

    public String generarToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return (username.equals(userDetails.getUsername()) && !esTokenExpirado(token));
    }

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
