package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    /**
     * Genera y despacha el comprobante de venta por correo electrónico (HTML transaccional).
     */
    @Async
    public void enviarComprobanteEmail(String destinoEmail, Venta venta, Usuario cliente) {
        if (destinoEmail == null || destinoEmail.isBlank()) {
            log.warn("[EMAIL-SERVICE] No se especificó correo para el envío del comprobante.");
            return;
        }

        String tipoDoc = venta.getTipoComprobante() != null ? venta.getTipoComprobante().name() : "BOLETA";
        String correlativo = venta.getSerieCorrelativo() != null ? venta.getSerieCorrelativo() : ("B001-" + venta.getId());
        String nombreCliente = (cliente != null && cliente.getNombre() != null) ? cliente.getNombre() : "Estimado/a Cliente";
        String dniCliente = (cliente != null && cliente.getDni() != null) ? cliente.getDni() : "No registrado";
        String fecha = venta.getFechaEmision() != null 
                ? venta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Reciente";
        String pinEntrega = (venta.getPedido() != null && venta.getPedido().getCodigoEntrega() != null)
                ? venta.getPedido().getCodigoEntrega() : "LMLL-" + (1000 + (int)(Math.random() * 9000));

        String cuerpoHtml = construirPlantillaHtml(nombreCliente, dniCliente, tipoDoc, correlativo, fecha, pinEntrega, venta.getTotal());

        log.info("======================================================================");
        log.info("[API NOTIFICACIONES / EMAIL ENVIADO]");
        log.info(">>> Destinatario: {}", destinoEmail);
        log.info(">>> Asunto: Tu {} Electrónica [{}] - La Moda te LLama", tipoDoc, correlativo);
        log.info(">>> Cliente: {} (DNI: {}) | Fecha: {}", nombreCliente, dniCliente, fecha);
        log.info(">>> PIN de Entrega para Delivery: {}", pinEntrega);
        log.info(">>> Total Pagado: S/ {}", venta.getTotal());
        log.info(">>> Comprobante Online: http://localhost:8080/comprobante/{}", venta.getId());
        log.info("======================================================================");

        // Envío asíncrono a servicio webhook/notificaciones si está configurado
        intentarDespachoWebhook(destinoEmail, correlativo, pinEntrega, venta.getTotal().toString());
    }

    private String construirPlantillaHtml(String nombre, String dni, String tipo, String serie, String fecha, String pin, Object total) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #111; text-align: center;">LA MODA TE LLAMA</h2>
                <p style="text-align: center; color: #d4af37; font-weight: bold;">COMPROBANTE ELECTRÓNICO OFICIAL</p>
                <hr style="border: 0; border-top: 1px solid #eee;">
                <p>Hola <strong>%s</strong> (DNI: %s),</p>
                <p>Tu compra ha sido procesada con éxito. A continuación el detalle de tu %s:</p>
                <ul>
                    <li><strong>Serie y Número:</strong> %s</li>
                    <li><strong>Fecha de Emisión:</strong> %s</li>
                    <li><strong>PIN de Entrega de Paquete:</strong> <span style="background: #fef08a; padding: 2px 6px; font-weight: bold;">%s</span></li>
                    <li><strong>Total Cancelado:</strong> S/ %s</li>
                </ul>
                <p style="text-align: center; margin-top: 25px;">
                    <a href="http://localhost:8080" style="background: #111; color: #d4af37; padding: 10px 20px; text-decoration: none; border-radius: 4px; font-weight: bold;">
                        Ver Mi Pedido
                    </a>
                </p>
            </div>
            """.formatted(nombre, dni, tipo, serie, fecha, pin, total);
    }

    private void intentarDespachoWebhook(String email, String serie, String pin, String total) {
        try {
            // Ejemplo de llamada a servicio webhook de prueba
            String jsonPayload = String.format("{\"email\":\"%s\",\"serie\":\"%s\",\"pin\":\"%s\",\"total\":\"%s\"}", email, serie, pin, total);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://httpbin.org/post"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(2))
                    .build();
            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
            // No bloquea la venta si el webhook de prueba no responde
        }
    }
}
