package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.config.JwtAuthenticationFilter;
import com.proyectomdweb.proyectomdweb.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        // Token CSRF con protección BREACH. Al poner el nombre del atributo en null el token se carga
        // en cada petición, así la cookie XSRF-TOKEN se escribe antes de que Thymeleaf renderice el
        // formulario (si se difiere, la respuesta puede estar ya enviada y la cookie se pierde).
        XorCsrfTokenRequestAttributeHandler csrfRequestHandler = new XorCsrfTokenRequestAttributeHandler();
        csrfRequestHandler.setCsrfRequestAttributeName(null);

        http
            // CSRF activo solo para las vistas Thymeleaf (formularios con th:action reciben el campo _csrf).
            // La API REST (/api/**) se autentica con JWT Bearer y se protege validando el Origin.
            // El token se guarda en cookie porque la aplicación no usa sesión de servidor (STATELESS).
            .csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
                .csrfTokenRequestHandler(csrfRequestHandler)
                .ignoringRequestMatchers("/api/**"))
            // CORS con los orígenes de app.cors.allowed-origins (bean corsConfigurationSource de CorsConfig)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // 0. Registrar un pedido por API exige un JWT válido
                .requestMatchers(HttpMethod.POST, "/api/pedidos/procesar").authenticated()

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
                    "/api/consulta/**",
                    "/auth/**",
                    "/api/auth/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/reclamaciones/**",
                    "/api/reclamaciones/**",
                    "/monitoreo/**",
                    "/actuator/**",
                    "/seguimiento/**",
                    "/api/tracking/**",
                    "/api/tallas/**",
                    "/manifest.json",
                    "/sw.js",
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
            
            // Rechaza peticiones POST/PUT/PATCH/DELETE a /api/** desde orígenes no permitidos
            .addFilterAfter(new OriginValidationFilter(corsConfigurationSource), CorsFilter.class)

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