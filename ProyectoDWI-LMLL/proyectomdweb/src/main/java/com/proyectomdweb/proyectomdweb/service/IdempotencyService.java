package com.proyectomdweb.proyectomdweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de Idempotencia para Pasarela de Pagos
 * Previene el doble cobro cuando el usuario hace clic múltiple o reintenta transacciones
 * Alumna: Antonela Jaimes
 */
@Service
@Slf4j
public class IdempotencyService {

    public static class IdempotencyRecord {
        private final Instant timestamp;
        private final Object response;

        public IdempotencyRecord(Object response) {
            this.timestamp = Instant.now();
            this.response = response;
        }

        public boolean isExpired(long minutes) {
            return Instant.now().isAfter(timestamp.plusSeconds(minutes * 60));
        }

        public Object getResponse() {
            return response;
        }
    }

    private final Map<String, IdempotencyRecord> cache = new ConcurrentHashMap<>();

    /**
     * Verifica si una clave ya fue procesada
     */
    public boolean exists(String key) {
        if (key == null || key.isBlank()) return false;
        limpiarExpirados();
        return cache.containsKey(key);
    }

    /**
     * Obtiene la respuesta en caché para una clave ya procesada
     */
    public Object getCachedResponse(String key) {
        if (key == null) return null;
        IdempotencyRecord record = cache.get(key);
        return (record != null) ? record.getResponse() : null;
    }

    /**
     * Registra una clave procesada con su respuesta
     */
    public void registerKey(String key, Object response) {
        if (key != null && !key.isBlank()) {
            cache.put(key, new IdempotencyRecord(response));
            log.info("[IDEMPOTENCY] Clave {} registrada con éxito.", key);
        }
    }

    private void limpiarExpirados() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(15));
    }
}
