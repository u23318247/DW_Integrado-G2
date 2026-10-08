package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.config.JwtAuthenticationFilter;
import com.proyectomdweb.proyectomdweb.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Las rutas de administración son para ADMIN y VENDEDOR
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "VENDEDOR")
                // 2. Rutas públicas: Tienda, API, Angular y todos los recursos estáticos
                .requestMatchers(
                    "/",
                    "/productos/**",
                    "/carrito/**",
                    "/pago/**",
                    "/comprobante/**",
                    "/pedidos/**",
                    "/auth/**",
                    "/api/**",
                    "/angular/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/favicon.ico"
                ).permitAll()
                
                // 3. Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            // Se apagan las sesiones de servidor nativas. 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Se agrega el filtro de Cookies/JWT antes del filtro nativo de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

   @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}