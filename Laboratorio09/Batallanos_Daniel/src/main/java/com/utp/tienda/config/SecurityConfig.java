package com.utp.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Tarea 6 - Configuracion de seguridad migrada a OAuth2 Resource Server (JWT).
 *
 * <p>Cambios respecto a la semana 8 (HTTP Basic):</p>
 * <ul>
 *   <li>Se deshabilita HTTP Basic y Form Login: ya no hay reto {@code Basic}.</li>
 *   <li>El cliente se autentica una vez en {@code POST /api/auth/login} y luego
 *       envia {@code Authorization: Bearer &lt;token&gt;}.</li>
 *   <li>El claim {@code roles} del token se convierte en autoridades de Spring
 *       Security mediante el {@link JwtAuthenticationConverter}.</li>
 *   <li>Se declara un {@link AuthenticationManager} explicito para que el
 *       endpoint de login valide las credenciales contra MySQL + BCrypt.</li>
 * </ul>
 *
 * <p>Matriz de autorizacion (el orden importa: gana la primera coincidencia):</p>
 * <pre>
 *   POST   /api/auth/login       -> permitAll()
 *          /api/publico/**       -> permitAll()
 *   GET    /api/productos/**     -> hasAnyRole(USER, ADMIN)
 *   POST   /api/productos/**     -> hasRole(ADMIN)
 *   PUT    /api/productos/**     -> hasRole(ADMIN)
 *   PATCH  /api/productos/**     -> hasRole(ADMIN)
 *   DELETE /api/productos/**     -> hasRole(ADMIN)
 *   GET    /api/admin/**         -> hasRole(ADMIN)
 *   POST   /api/inventario/ajustes -> hasRole(ADMIN)
 *          /api/inventario/**    -> hasAnyRole(USER, ADMIN)
 *          /api/auth/**         -> authenticated  (por ejemplo /api/auth/me)
 *   cualquier otra ruta         -> authenticated
 * </pre>
 */
@Configuration
public class SecurityConfig {

    /** Codificador de contrasenas: BCrypt (hashes con prefijo $2a$ / $2b$ / $2y$). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticacion usado por {@link #authenticationManager}: valida
     * el usuario contra MySQL (CustomUserDetailsService) y verifica el hash BCrypt.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * AuthenticationManager que consume el endpoint de login.
     * Se declara como bean para inyectarlo en el AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    /**
     * Convierte el claim {@code roles} del JWT en autoridades de Spring Security.
     *
     * <p>{@code setAuthorityPrefix("")} es indispensable: los roles ya se guardan
     * como {@code ROLE_USER} / {@code ROLE_ADMIN}, y Spring Security antepondria
     * {@code SCOPE_} por defecto, provocando authorities equivocadas.</p>
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    /**
     * Cadena de filtros unica: validacion del JWT + reglas de autorizacion por URL.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
                // API stateless: sin cookies ni token CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Sin sesiones HTTP: el estado viaja en el token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Ya no se usa HTTP Basic ni formulario: el reto es 401 Bearer
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // --- Autenticacion: el login es publico ---
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        // --- Endpoints publicos ---
                        .requestMatchers("/api/publico/**").permitAll()

                        // --- Productos: lectura para USER y ADMIN ---
                        .requestMatchers(HttpMethod.GET, "/api/productos/**")
                        .hasAnyRole("USER", "ADMIN")

                        // --- Productos: escritura exclusiva de ADMIN ---
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // --- Reportes administrativos: solo ADMIN ---
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")

                        // --- Inventario: primero la regla especifica de ADMIN ---
                        .requestMatchers(HttpMethod.POST, "/api/inventario/ajustes").hasRole("ADMIN")
                        .requestMatchers("/api/inventario/**").hasAnyRole("USER", "ADMIN")

                        // --- Perfil autenticado (/api/auth/me) ---
                        .requestMatchers("/api/auth/**").authenticated()

                        // --- Cualquier otra ruta requiere token valido ---
                        .anyRequest().authenticated())

                // Resource Server: verifica firma y vigencia, y aplica el convertidor
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()));

        return http.build();
    }
}