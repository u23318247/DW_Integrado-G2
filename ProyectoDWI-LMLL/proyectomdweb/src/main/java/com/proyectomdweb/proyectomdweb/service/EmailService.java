package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    /**
     * Simula y despacha la emisión del comprobante (Factura / Boleta) al correo del cliente.
     * Compatible de inmediato sin requerir librerías externas o conexión a servidores SMTP externos.
     */
    @Async
    public void enviarComprobanteEmail(String destinoEmail, Venta venta, Usuario cliente) {
        if (destinoEmail == null || destinoEmail.isBlank()) {
            log.warn("[EMAIL-SERVICE] No se especificó correo para el envío del comprobante.");
            return;
        }

        String tipoDoc = venta.getTipoComprobante() != null ? venta.getTipoComprobante().name() : "BOLETA";
        String correlativo = venta.getSerieCorrelativo() != null ? venta.getSerieCorrelativo() : ("B001-" + venta.getId());
        String nombreCliente = cliente != null && cliente.getNombre() != null ? cliente.getNombre() : "Cliente";
        String fecha = venta.getFechaEmision() != null 
                ? venta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Reciente";
        String pinEntrega = (venta.getPedido() != null && venta.getPedido().getCodigoEntrega() != null)
                ? venta.getPedido().getCodigoEntrega() : "Sin PIN";

        log.info("======================================================================");
        log.info("[EMAIL ENVIADO] >>> Destinatario: {}", destinoEmail);
        log.info("Asunto: Tu {} Electrónica [{}] - La Moda te LLama", tipoDoc, correlativo);
        log.info("Cliente: {} | Fecha: {}", nombreCliente, fecha);
        log.info("PIN de Entrega asignado: {}", pinEntrega);
        log.info("Total Comprobante: S/ {}", venta.getTotal());
        log.info("Enlace de visualización: http://localhost:8080/comprobante/{}", venta.getId());
        log.info("======================================================================");
    }
}
