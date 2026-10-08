package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.EstadoPedido;
import com.proyectomdweb.proyectomdweb.model.Pedido;
import com.proyectomdweb.proyectomdweb.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final PedidoRepository pedidoRepository;

    // Listar todos los pedidos para el vendedor/administrador
    @GetMapping
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByIdDesc();
        model.addAttribute("pedidos", pedidos);
        return "admin/lista-pedidos";
    }

    // Marcar pedido como ENVIADO (en camino al cliente)
    @PostMapping("/despachar/{id}")
    public String despacharPedido(@PathVariable Long id, RedirectAttributes flash) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstado(EstadoPedido.ENVIADO);
            pedidoRepository.save(pedido);
            flash.addFlashAttribute("mensajeExito", "Pedido #" + id + " marcado como ENVIADO con éxito.");
        }
        return "redirect:/admin/pedidos";
    }

    // Comprobar y confirmar entrega con PIN / Código Secreto
    @PostMapping("/entregar/{id}")
    public String entregarPedidoConPin(
            @PathVariable Long id,
            @RequestParam("pinIngresado") String pinIngresado,
            RedirectAttributes flash) {
        
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            flash.addFlashAttribute("mensajeError", "El pedido no existe.");
            return "redirect:/admin/pedidos";
        }

        if (pedido.getCodigoEntrega() != null && pedido.getCodigoEntrega().trim().equals(pinIngresado.trim())) {
            pedido.setEstado(EstadoPedido.ENTREGADO);
            pedidoRepository.save(pedido);
            flash.addFlashAttribute("mensajeExito", "¡Excelente! PIN verificado. Pedido #" + id + " confirmado como ENTREGADO.");
        } else {
            flash.addFlashAttribute("mensajeError", "PIN incorrecto para el pedido #" + id + ". Verifique con el cliente.");
        }

        return "redirect:/admin/pedidos";
    }
}
