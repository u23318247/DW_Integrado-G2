package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/consulta")
@RequiredArgsConstructor
public class ConsultaDniController {

    private final UsuarioRepository usuarioRepository;

    @Value("${reniec.api.token:}")
    private String reniecApiToken;

    private static final HttpClient HTTP_CLIENT = crearHttpClientConfiable();

    private static HttpClient crearHttpClientConfiable() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        } catch (Exception e) {
            log.error("Error al inicializar SSL para cliente DNI: {}", e.getMessage());
            return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> consultarDni(@PathVariable String dni) {
        Map<String, Object> respuesta = new HashMap<>();
        String dniLimpio = (dni != null) ? dni.trim().replaceAll("\\D", "") : "";

        if (dniLimpio.length() != 8) {
            respuesta.put("encontrado", false);
            respuesta.put("mensaje", "El DNI debe contener exactamente 8 dígitos.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        // 1. Buscar en la base de datos local de clientes y usuarios registrados
        Optional<Usuario> usuarioOpt = usuarioRepository.findByDni(dniLimpio);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (u.getNombre() != null && !u.getNombre().isBlank()) {
                String nombreCompleto = u.getNombre().trim();
                String[] partes = separarNombresYApellidos(nombreCompleto);

                respuesta.put("dni", u.getDni());
                respuesta.put("nombres", partes[0]);
                respuesta.put("apellidos", partes[1]);
                respuesta.put("nombre", nombreCompleto);
                respuesta.put("encontrado", true);
                respuesta.put("origen", "Base de Datos de Clientes");
                return ResponseEntity.ok(respuesta);
            }
        }

        // 2. Consulta REAL en vivo al servicio oficial de RENIEC (apis.net.pe)
        Map<String, Object> resultadoReniec = consultarReniecEnVivo(dniLimpio);
        if (resultadoReniec != null && Boolean.TRUE.equals(resultadoReniec.get("encontrado"))) {
            return ResponseEntity.ok(resultadoReniec);
        }

        // 3. Si no se encontró en RENIEC o falló la conexión externa:
        // No inventamos nombres ficticios.
        respuesta.put("dni", dniLimpio);
        respuesta.put("encontrado", false);
        respuesta.put("mensaje", "No se encontró el DNI en los registros de RENIEC. Por favor, escribe tus nombres y apellidos manualmente.");
        return ResponseEntity.ok(respuesta);
    }

    private Map<String, Object> consultarReniecEnVivo(String dni) {
        // Intento con apis.net.pe v2 si hay token configurado
        if (reniecApiToken != null && !reniecApiToken.isBlank()) {
            Map<String, Object> v2Res = realizarPeticionHttp(
                "https://api.apis.net.pe/v2/reniec/dni?numero=" + dni,
                "Bearer " + reniecApiToken.trim(),
                dni
            );
            if (v2Res != null) return v2Res;
        }

        // Intento con apis.net.pe v1 (abierto para consultas DNI)
        Map<String, Object> v1Res = realizarPeticionHttp(
            "https://api.apis.net.pe/v1/dni?numero=" + dni,
            null,
            dni
        );
        if (v1Res != null) return v1Res;

        // Si hubo microdemora o rate limit (429), reintento tras 400ms
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}

        return realizarPeticionHttp(
            "https://api.apis.net.pe/v1/dni?numero=" + dni,
            null,
            dni
        );
    }

    private Map<String, Object> realizarPeticionHttp(String urlStr, String authHeader, String fallbackDni) {
        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(6))
                .GET();

            if (authHeader != null && !authHeader.isBlank()) {
                reqBuilder.header("Authorization", authHeader);
            }

            HttpResponse<String> response = HTTP_CLIENT.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
                String body = response.body().trim();
                if (body.startsWith("{")) {
                    String nombresRaw  = extraerCampoJson(body, "nombres");
                    String paternoRaw  = extraerCampoJson(body, "apellidoPaterno");
                    String maternoRaw  = extraerCampoJson(body, "apellidoMaterno");
                    String nombreRaw   = extraerCampoJson(body, "nombre");

                    String nombres = "";
                    String apellidos = "";

                    if (nombresRaw != null && !nombresRaw.isBlank()) {
                        nombres = capitalizarPalabras(nombresRaw);
                    }
                    StringBuilder sbAp = new StringBuilder();
                    if (paternoRaw != null && !paternoRaw.isBlank()) {
                        sbAp.append(capitalizarPalabras(paternoRaw)).append(" ");
                    }
                    if (maternoRaw != null && !maternoRaw.isBlank()) {
                        sbAp.append(capitalizarPalabras(maternoRaw));
                    }
                    apellidos = sbAp.toString().trim();

                    if (nombres.isBlank() && nombreRaw != null && !nombreRaw.isBlank()) {
                        String[] sep = separarNombresYApellidos(capitalizarPalabras(nombreRaw));
                        nombres = sep[0];
                        apellidos = sep[1];
                    }

                    String nombreCompleto = (nombres + " " + apellidos).trim();

                    if (!nombreCompleto.isBlank()) {
                        Map<String, Object> resultado = new HashMap<>();
                        String dniRes = extraerCampoJson(body, "numeroDocumento");
                        if (dniRes == null || dniRes.isBlank()) {
                            dniRes = fallbackDni;
                        }

                        resultado.put("dni", dniRes);
                        resultado.put("nombres", nombres);
                        resultado.put("apellidos", apellidos);
                        resultado.put("nombre", nombreCompleto);
                        resultado.put("encontrado", true);
                        resultado.put("origen", "RENIEC Oficial");
                        return resultado;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Fallo consultando servicio RENIEC {}: {}", urlStr, e.getMessage());
        }
        return null;
    }

    private String[] separarNombresYApellidos(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return new String[]{"", ""};
        }
        String[] partes = nombreCompleto.trim().split("\\s+");
        if (partes.length == 1) {
            return new String[]{partes[0], ""};
        } else if (partes.length == 2) {
            return new String[]{partes[0], partes[1]};
        } else if (partes.length == 3) {
            return new String[]{partes[0], partes[1] + " " + partes[2]};
        } else {
            // 4 o más palabras: primeros dos nombres, el resto apellidos
            String nombres = partes[0] + " " + partes[1];
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < partes.length; i++) {
                sb.append(partes[i]).append(" ");
            }
            return new String[]{nombres, sb.toString().trim()};
        }
    }

    private String extraerCampoJson(String json, String campo) {
        if (json == null || campo == null) return null;
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String capitalizarPalabras(String texto) {
        if (texto == null || texto.isBlank()) return "";
        String[] partes = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) {
                sb.append(p.substring(1));
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
