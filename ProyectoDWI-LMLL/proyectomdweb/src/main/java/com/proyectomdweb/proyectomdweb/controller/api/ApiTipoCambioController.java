package com.proyectomdweb.proyectomdweb.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/tipo-cambio")
public class ApiTipoCambioController {

    private static final BigDecimal FALLBACK_PEN = new BigDecimal("3.75");
    private static final Pattern PEN_PATTERN = Pattern.compile("\"PEN\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    private volatile BigDecimal tasaCache = FALLBACK_PEN;
    private volatile String fuenteCache = "referencial";
    private volatile Instant ultimaActualizacion = Instant.EPOCH;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTipoCambio() {
        refrescarSiExpiro();
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("monedaBase", "USD");
        res.put("monedaDestino", "PEN");
        res.put("tasa", tasaCache);
        res.put("compra", tasaCache.subtract(new BigDecimal("0.02")).setScale(3, RoundingMode.HALF_UP));
        res.put("venta", tasaCache.add(new BigDecimal("0.02")).setScale(3, RoundingMode.HALF_UP));
        res.put("fuente", fuenteCache);
        res.put("actualizadoEn", ultimaActualizacion.toString());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/convertir")
    public ResponseEntity<Map<String, Object>> convertir(
            @RequestParam BigDecimal monto,
            @RequestParam(defaultValue = "PEN") String de) {
        refrescarSiExpiro();
        BigDecimal resultado;
        String hacia;
        if ("USD".equalsIgnoreCase(de)) {
            resultado = monto.multiply(tasaCache).setScale(2, RoundingMode.HALF_UP);
            hacia = "PEN";
        } else {
            resultado = monto.divide(tasaCache, 2, RoundingMode.HALF_UP);
            hacia = "USD";
        }
        return ResponseEntity.ok(Map.of(
                "montoOriginal", monto,
                "monedaOrigen", de.toUpperCase(),
                "montoConvertido", resultado,
                "monedaDestino", hacia,
                "tasa", tasaCache
        ));
    }

    private synchronized void refrescarSiExpiro() {
        if (Duration.between(ultimaActualizacion, Instant.now()).toMinutes() < 60) {
            return;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://open.er-api.com/v6/latest/USD"))
                    .timeout(Duration.ofSeconds(4))
                    .GET()
                    .build();
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Matcher m = PEN_PATTERN.matcher(resp.body());
                if (m.find()) {
                    tasaCache = new BigDecimal(m.group(1)).setScale(4, RoundingMode.HALF_UP);
                    fuenteCache = "open.er-api.com";
                    ultimaActualizacion = Instant.now();
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo consultar tipo de cambio externo, usando caché/fallback: {}", e.getMessage());
        }
        ultimaActualizacion = Instant.now();
    }
}
