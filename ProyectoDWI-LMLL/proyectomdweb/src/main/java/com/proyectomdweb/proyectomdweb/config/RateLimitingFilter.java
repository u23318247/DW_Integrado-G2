package com.proyectomdweb.proyectomdweb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de Seguridad para Rate Limiting (Protección Anti-Bot y Anti-Fuerza Bruta)
 * Limita peticiones excesivas a endpoints críticos (/api/reniec, /api/auth, /api/pagos)
 * Alumna: Antonela Jaimes
 */
@Component
@Order(1)
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 25;

    private static class RequestCount {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile Instant windowStart = Instant.now();

        public boolean tryAcquire() {
            Instant now = Instant.now();
            if (now.isAfter(windowStart.plusSeconds(60))) {
                synchronized (this) {
                    if (now.isAfter(windowStart.plusSeconds(60))) {
                        count.set(0);
                        windowStart = now;
                    }
                }
            }
            return count.incrementAndGet() <= MAX_REQUESTS_PER_MINUTE;
        }
    }

    private final Map<String, RequestCount> clientRequests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Aplicar solo a endpoints sensibles
        if (path.startsWith("/api/reniec") || path.startsWith("/api/auth") || path.startsWith("/api/pagos")) {
            String clientIp = getClientIP(request);
            RequestCount counter = clientRequests.computeIfAbsent(clientIp, k -> new RequestCount());

            if (!counter.tryAcquire()) {
                log.warn("[RATE-LIMIT] IP {} excedió el límite de peticiones en {}", clientIp, path);
                response.setStatus(429); // 429 Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("""
                    {
                        "status": 429,
                        "error": "Too Many Requests",
                        "mensaje": "Límite de solicitudes superado por seguridad (Anti-DDoS). Espere 1 minuto antes de reintentar.",
                        "ip": "%s"
                    }
                """.formatted(clientIp));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
