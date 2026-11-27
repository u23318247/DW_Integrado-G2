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

/**
 * Filtro de Seguridad para Rate Limiting (Token Bucket por IP)
 * Limita peticiones excesivas a endpoints críticos (/api/reniec, /api/auth, /api/pagos)
 */
@Component
@Order(1)
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_TOKENS = 25;
    private static final long REFILL_MILLIS = 60_000L;

    private static class TokenBucket {
        private double tokens = MAX_TOKENS;
        private long lastRefillMs = Instant.now().toEpochMilli();

        synchronized boolean tryConsume() {
            long now = Instant.now().toEpochMilli();
            long elapsed = now - lastRefillMs;
            if (elapsed > 0) {
                double refill = (elapsed / (double) REFILL_MILLIS) * MAX_TOKENS;
                tokens = Math.min(MAX_TOKENS, tokens + refill);
                lastRefillMs = now;
            }
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/reniec") || path.startsWith("/api/auth") || path.startsWith("/api/pagos")) {
            String clientIp = getClientIP(request);
            TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket());

            if (!bucket.tryConsume()) {
                log.warn("[RATE-LIMIT] IP {} excedió el límite de peticiones en {}", clientIp, path);
                response.setStatus(429);
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
