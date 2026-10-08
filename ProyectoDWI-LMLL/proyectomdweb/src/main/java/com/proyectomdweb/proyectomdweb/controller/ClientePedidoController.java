package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.EstadoPedido;
import com.proyectomdweb.proyectomdweb.model.Pedido;
import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.repository.PedidoRepository;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mis-pedidos")
@RequiredArgsConstructor
public class ClientePedidoController {

    private final PedidoRepository pedidoRepository;
    private final VentaRepository  ventaRepository;
    private final UsuarioService   usuarioService;

    // Vista de "Mis Pedidos" para el comprador
    @GetMapping
    public String verMisPedidos(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(principal.getName());
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByIdDesc(usuario.getId());

        // Asociar cada pedido con su venta (para mostrar enlace a Boleta/Factura)
        Map<Long, Venta> ventasPorPedido = new HashMap<>();
        for (Pedido p : pedidos) {
            ventaRepository.findByPedidoId(p.getId()).ifPresent(v -> ventasPorPedido.put(p.getId(), v));
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("ventasPorPedido", ventasPorPedido);
        model.addAttribute("usuario", usuario);

        return "mis-pedidos";
    }

    // Botón del cliente: "Confirmar que ya recibí mi prenda"
    @PostMapping("/confirmar-recepcion/{id}")
    public String confirmarRecepcion(
            @PathVariable Long id, 
            Principal principal,
            RedirectAttributes flash) {
        
        if (principal == null) {
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(principal.getName());
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null && pedido.getUsuarioId().equals(usuario.getId())) {
            pedido.setEstado(EstadoPedido.ENTREGADO);
            pedidoRepository.save(pedido);
            flash.addFlashAttribute("mensajeExito", "¡Genial! Confirmaste la recepción del Pedido #" + id + ". ¡Disfruta tu prenda!");
        } else {
            flash.addFlashAttribute("mensajeError", "No tienes permisos sobre este pedido.");
        }

        return "redirect:/mis-pedidos";
    }
}
