package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.dtos.CheckoutRequestDTO;
import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;
import com.proyectomdweb.proyectomdweb.service.VentaPresentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor 
public class PedidoController {

    private final VentaPresentacionService ventaService;
    private final UsuarioService usuarioService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(
            @RequestBody CheckoutRequestDTO request,
            Principal principal) {
        try {
            String email = (principal != null) ? principal.getName() : "invitado@tienda.com";
            Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(email);

            Venta venta = ventaService.crearVentaInicialPendiente(request.total(), "BOLETA", usuario);
            ventaService.completarVentaExitosa(venta, request.metodoPago());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Venta y pedido registrados exitosamente",
                    "ventaId", venta.getId(),
                    "serieCorrelativo", venta.getSerieCorrelativo(),
                    "total", venta.getTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error al procesar el pedido: " + e.getMessage()
            ));
        }
    }
}
