package com.utp.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Endpoint publico
                .requestMatchers("/api/publico/**")
                .permitAll()

                // Consultar productos
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/productos/**"
                )
                .hasAnyRole("USER", "ADMIN")

                // Crear productos
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/productos/**"
                )
                .hasRole("ADMIN")

                // Actualizar productos
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/productos/**"
                )
                .hasRole("ADMIN")

                // Actualizar parcialmente
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/productos/**"
                )
                .hasRole("ADMIN")

                // Eliminar productos
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/productos/**"
                )
                .hasRole("ADMIN")

                // Cualquier otro recurso requiere autenticacion
                .anyRequest()
                .authenticated()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}