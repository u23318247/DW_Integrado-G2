package com.proyectomdweb.proyectomdweb.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
public class ApiPagoController {

    private final Map<String, Map<String, Object>> intentos = new ConcurrentHashMap<>();

    @PostMapping("/crear-intento")
    public ResponseEntity<Map<String, Object>> crearIntento(@RequestBody Map<String, Object> body) {
        String metodo = String.valueOf(body.getOrDefault("metodo", "YAPE")).toUpperCase();
        BigDecimal monto = new BigDecimal(String.valueOf(body.getOrDefault("monto", "0")));
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El monto debe ser mayor a 0."));
        }

        String codigoOperacion = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> intento = new LinkedHashMap<>();
        intento.put("codigoOperacion", codigoOperacion);
        intento.put("metodo", metodo);
        intento.put("monto", monto);
        intento.put("moneda", "PEN");
        intento.put("estado", "PENDIENTE");
        intento.put("qrPayload", "LMLL://pagar?op=" + codigoOperacion + "&monto=" + monto + "&metodo=" + metodo);
        intento.put("creadoEn", LocalDateTime.now().toString());

        intentos.put(codigoOperacion, intento);
        return ResponseEntity.ok(intento);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmar(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigoOperacion");
        Map<String, Object> intento = intentos.get(codigo);
        if (intento == null) {
            return ResponseEntity.notFound().build();
        }
        intento.put("estado", "APROBADO");
        intento.put("confirmadoEn", LocalDateTime.now().toString());
        return ResponseEntity.ok(intento);
    }

    @GetMapping("/{codigoOperacion}")
    public ResponseEntity<Map<String, Object>> consultar(@PathVariable String codigoOperacion) {
        Map<String, Object> intento = intentos.get(codigoOperacion);
        return intento != null ? ResponseEntity.ok(intento) : ResponseEntity.notFound().build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> webhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook de pasarela recibido: {}", payload);
        String codigo = String.valueOf(payload.getOrDefault("codigoOperacion", ""));
        if (intentos.containsKey(codigo)) {
            intentos.get(codigo).put("estado", String.valueOf(payload.getOrDefault("estado", "APROBADO")));
        }
        return ResponseEntity.ok(Map.of("recibido", true, "codigoOperacion", codigo));
    }
}
