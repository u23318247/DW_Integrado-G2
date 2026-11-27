package com.proyectomdweb.proyectomdweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de Idempotencia para Pasarela de Pagos con TTL de 15 minutos.
 * Previene el doble cobro cuando el usuario hace clic múltiple o reintenta transacciones.
 */
@Service
@Slf4j
public class IdempotencyService {

    private static final Duration TTL = Duration.ofMinutes(15);

    public static class IdempotencyRecord {
        private final Instant timestamp;
        private final Object response;

        public IdempotencyRecord(Object response) {
            this.timestamp = Instant.now();
            this.response = response;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(timestamp.plus(TTL));
        }

        public Object getResponse() {
            return response;
        }
    }

    private final Map<String, IdempotencyRecord> cache = new ConcurrentHashMap<>();

    public boolean exists(String key) {
        if (key == null || key.isBlank()) return false;
        limpiarExpirados();
        return cache.containsKey(key);
    }

    public Object getCachedResponse(String key) {
        if (key == null || key.isBlank()) return null;
        limpiarExpirados();
        IdempotencyRecord record = cache.get(key);
        return (record != null) ? record.getResponse() : null;
    }

    public void registerKey(String key, Object response) {
        if (key != null && !key.isBlank()) {
            cache.put(key, new IdempotencyRecord(response));
            log.info("[IDEMPOTENCY] Clave {} registrada con TTL {} min.", key, TTL.toMinutes());
        }
    }

    private void limpiarExpirados() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
