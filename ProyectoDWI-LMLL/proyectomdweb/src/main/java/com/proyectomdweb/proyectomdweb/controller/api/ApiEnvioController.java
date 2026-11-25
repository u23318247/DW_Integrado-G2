package com.proyectomdweb.proyectomdweb.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/envio")
public class ApiEnvioController {

    private static final BigDecimal UMBRAL_ENVIO_GRATIS = new BigDecimal("299.00");
    private static final Pattern DISPLAY_NAME_PATTERN = Pattern.compile("\"display_name\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @GetMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcularTarifa(
            @RequestParam(defaultValue = "Lima") String distrito,
            @RequestParam(defaultValue = "0") BigDecimal subtotal) {

        String d = distrito.trim().toLowerCase();
        BigDecimal costo;
        String zona;
        String tiempoEstimado;

        if (subtotal.compareTo(UMBRAL_ENVIO_GRATIS) >= 0) {
            costo = BigDecimal.ZERO;
            zona = "Envío gratuito por compra >= S/ 299.00";
            tiempoEstimado = "24 - 48 horas";
        } else if (d.contains("callao") || d.contains("bellavista") || d.contains("la perla")) {
            costo = new BigDecimal("15.00");
            zona = "Callao";
            tiempoEstimado = "24 - 48 horas";
        } else if (d.contains("miraflores") || d.contains("san isidro") || d.contains("surco")
                || d.contains("lima") || d.contains("san borja") || d.contains("la molina")
                || d.contains("jesus maria") || d.contains("jesús maría") || d.contains("magdalena")
                || d.contains("lince") || d.contains("barranco") || d.contains("los olivos")
                || d.contains("san miguel") || d.contains("pueblo libre")) {
            costo = new BigDecimal("12.00");
            zona = "Lima Metropolitana";
            tiempoEstimado = "24 horas";
        } else {
            costo = new BigDecimal("25.00");
            zona = "Provincias (Courier Nacional)";
            tiempoEstimado = "3 - 5 días hábiles";
        }

        return ResponseEntity.ok(Map.of(
                "distrito", distrito,
                "zona", zona,
                "costoEnvio", costo,
                "envioGratisDesde", UMBRAL_ENVIO_GRATIS,
                "tiempoEstimado", tiempoEstimado
        ));
    }

    @GetMapping("/buscar-direccion")
    public ResponseEntity<List<Map<String, Object>>> buscarDireccion(@RequestParam String q) {
        if (q == null || q.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<Map<String, Object>> resultados = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(q.trim(), StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + encoded
                    + "&countrycodes=pe&format=json&limit=5&addressdetails=1";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "LMLL-Ecommerce/2.0 (contacto@lamodatellama.pe)")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Matcher m = DISPLAY_NAME_PATTERN.matcher(resp.body());
                while (m.find() && resultados.size() < 5) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("direccion", m.group(1).replace("\\\"", "\""));
                    resultados.add(entry);
                }
            }
        } catch (Exception e) {
            log.warn("Error consultando Nominatim: {}", e.getMessage());
        }
        return ResponseEntity.ok(resultados);
    }
}
