package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.dtos.CheckoutRequestDTO;
import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;
import com.proyectomdweb.proyectomdweb.service.VentaPresentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final VentaPresentacionService ventaService;
    private final UsuarioService usuarioService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody CheckoutRequestDTO request) {
        // El usuario se toma del SecurityContext, que JwtAuthenticationFilter llena
        // solo después de validar la firma y la expiración del JWT.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Debe iniciar sesión para registrar un pedido"
            ));
        }

        try {
            Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(auth.getName());

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
