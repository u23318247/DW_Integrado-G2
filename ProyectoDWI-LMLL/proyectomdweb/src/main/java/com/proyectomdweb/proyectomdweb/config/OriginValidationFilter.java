package com.proyectomdweb.proyectomdweb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Valida la cabecera Origin en las peticiones que modifican datos de /api/**.
 *
 * CORS solo impide que el navegador LEA la respuesta: una petición POST/PUT/DELETE
 * desde otro sitio igual llega al servidor y se ejecuta con la cookie jwt_token.
 * Como /api/** no usa token CSRF (lo consume Angular con JWT Bearer), este filtro
 * rechaza con 403 las peticiones cuyo Origin no sea el propio servidor ni esté en
 * app.cors.allowed-origins.
 *
 * Si la petición no trae Origin (Postman, curl, servidor a servidor) se deja pasar:
 * no es un navegador, así que no hay riesgo de CSRF y el JWT sigue siendo obligatorio.
 *
 * No es @Component para que solo se registre dentro de la cadena de Spring Security.
 */
public class OriginValidationFilter extends OncePerRequestFilter {

    private static final Set<String> METODOS_SEGUROS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final CorsConfigurationSource corsConfigurationSource;

    public OriginValidationFilter(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/")
                || METODOS_SEGUROS.contains(request.getMethod().toUpperCase());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        if (origin == null || esMismoOrigen(origin, request) || esOrigenPermitido(origin, request)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"Origen no permitido\"}");
    }

    private boolean esOrigenPermitido(String origin, HttpServletRequest request) {
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);
        return config != null && config.checkOrigin(origin) != null;
    }

    /** Compara el Origin con esquema://host:puerto del propio servidor (páginas Thymeleaf). */
    private boolean esMismoOrigen(String origin, HttpServletRequest request) {
        String esquema = request.getScheme();
        int puerto = request.getServerPort();
        boolean puertoPorDefecto = ("http".equalsIgnoreCase(esquema) && puerto == 80)
                || ("https".equalsIgnoreCase(esquema) && puerto == 443);
        String propio = esquema + "://" + request.getServerName() + (puertoPorDefecto ? "" : ":" + puerto);
        return propio.equalsIgnoreCase(origin);
    }
}
