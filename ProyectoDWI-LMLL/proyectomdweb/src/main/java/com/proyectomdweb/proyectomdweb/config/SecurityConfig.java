package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.config.JwtAuthenticationFilter;
import com.proyectomdweb.proyectomdweb.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Estadísticas e Ingresos son de acceso exclusivo para ADMIN (Clean Code RBAC)
                .requestMatchers("/admin/estadisticas/**").hasRole("ADMIN")
                // 2. Gestión de Productos y Stock para ADMIN, VENDEDOR y ALMACENERO
                .requestMatchers("/admin/productos/**").hasAnyRole("ADMIN", "VENDEDOR", "ALMACENERO")
                // 3. Gestión de Pedidos y Entregas para ADMIN, VENDEDOR, ALMACENERO y REPARTIDOR
                .requestMatchers("/admin/pedidos/**").hasAnyRole("ADMIN", "VENDEDOR", "ALMACENERO", "REPARTIDOR")
                // 4. Panel general administrativo
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "VENDEDOR", "ALMACENERO", "REPARTIDOR")
                
                // 5. Área privada de seguimiento
                .requestMatchers("/mis-pedidos/**").authenticated()

                // 6. Rutas públicas de la tienda virtual: inicio, ropa, catálogo, carrito, pago, comprobante, Angular y recursos estáticos
                .requestMatchers(
                    "/",
                    "/productos/**",
                    "/angular/**",
                    "/api/productos/**",
                    "/carrito/**",
                    "/pago/**",
                    "/comprobante/**",
                    "/auth/**",
                    "/api/auth/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                
                // 7. El resto de navegación por defecto es pública
                .anyRequest().permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/api/")) {
                        response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                    } else {
                        response.sendRedirect("/auth/login");
                    }
                })
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