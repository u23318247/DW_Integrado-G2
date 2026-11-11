package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.repository.UsuarioRepository;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador de Tracking y Seguimiento de Delivery en Vivo con Mapa
 * Alumna: Antonela Jaimes
 */
@Controller
@RequiredArgsConstructor
public class TrackingController {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/seguimiento")
    public String verPaginaSeguimiento(@RequestParam(value = "codigo", required = false) String codigo, Model model) {
        model.addAttribute("codigoInicial", codigo != null ? codigo : "");
        return "seguimiento";
    }

    @GetMapping("/api/tracking/{codigo}")
    @ResponseBody
    public ResponseEntity<?> consultarEstadoTracking(@PathVariable String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código de pedido o venta requerido."));
        }

        String codigoLimpio = codigo.trim().toUpperCase().replace("LMLL-VENTA-", "").replace("PED-", "");
        Long idNum = null;
        try {
            idNum = Long.parseLong(codigoLimpio);
        } catch (NumberFormatException ignored) {}

        String estado = "EN_CAMINO";
        String direccion = "Av. Javier Prado Este 4200, Surco, Lima";
        String cliente = "Cliente La Moda te LLama";
        String courier = "Carlos Huamán (Moto Envíos Express)";
        String pinEntrega = "LMLL-7842";
        double latAlmacen = -12.0863;
        double lngAlmacen = -77.0012;
        double latDestino = -12.0520;
        double lngDestino = -77.0428;

        if (idNum != null) {
            Optional<Venta> ventaOpt = ventaRepository.findById(idNum);
            if (ventaOpt.isPresent()) {
                Venta v = ventaOpt.get();
                if (v.getPedido() != null) {
                    if (v.getPedido().getDireccionEnvio() != null && !v.getPedido().getDireccionEnvio().isBlank()) {
                        direccion = v.getPedido().getDireccionEnvio();
                    }
                    if (v.getPedido().getUsuarioId() != null) {
                        Optional<Usuario> userOpt = usuarioRepository.findById(v.getPedido().getUsuarioId());
                        if (userOpt.isPresent()) {
                            cliente = userOpt.get().getNombre();
                            if ((direccion == null || direccion.isBlank()) && userOpt.get().getDireccion() != null) {
                                direccion = userOpt.get().getDireccion();
                            }
                        }
                    }
                    estado = v.getPedido().getEstado() != null ? v.getPedido().getEstado().name() : "EN_CAMINO";
                    if (v.getPedido().getCodigoEntrega() != null) {
                        pinEntrega = v.getPedido().getCodigoEntrega();
                    }
                }
            }
        }

        double progreso = 0.65;
        double latRepartidor = latAlmacen + (latDestino - latAlmacen) * progreso;
        double lngRepartidor = lngAlmacen + (lngDestino - lngAlmacen) * progreso;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("codigo", codigo);
        resp.put("estado", estado);
        resp.put("cliente", cliente);
        resp.put("direccionEntrega", direccion);
        resp.put("repartidor", courier);
        resp.put("pinEntrega", pinEntrega);
        resp.put("tiempoEstimadoMinutos", 14);
        resp.put("latRepartidor", latRepartidor);
        resp.put("lngRepartidor", lngRepartidor);
        resp.put("latDestino", latDestino);
        resp.put("lngDestino", lngDestino);
        resp.put("pasos", Map.of(
                "1_confirmado", true,
                "2_empaquetado", true,
                "3_en_ruta", true,
                "4_entregado", false
        ));

        return ResponseEntity.ok(resp);
    }
}
