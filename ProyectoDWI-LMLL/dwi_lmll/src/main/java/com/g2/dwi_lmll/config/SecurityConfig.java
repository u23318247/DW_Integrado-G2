package com.g2.dwi_lmll.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Primera entrega: API abierta para pruebas con Postman.
 * Sin login, sin CSRF (para permitir POST/PUT/DELETE) y sin formulario de autenticacion.
 * Cuando se implemente el login, aqui se restringiran las rutas.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // consola H2
        return http.build();
    }
}
