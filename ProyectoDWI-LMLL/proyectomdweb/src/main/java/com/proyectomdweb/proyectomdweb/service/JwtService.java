package com.proyectomdweb.proyectomdweb.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Llave secreta segura generada para firmar los tokens
    private static final Key SECRET_KEY       = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 1800000; // 15 min en milisegundos

    public String generarToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
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
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
