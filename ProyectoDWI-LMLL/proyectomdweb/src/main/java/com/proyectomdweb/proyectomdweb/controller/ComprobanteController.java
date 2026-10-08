package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.repository.UsuarioRepository;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import com.proyectomdweb.proyectomdweb.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comprobante")
@RequiredArgsConstructor
public class ComprobanteController {

    private final VentaRepository   ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService      emailService;

    @GetMapping("/{id}")
    public String verComprobante(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            return "redirect:/";
        }

        Usuario cliente = null;
        if (venta.getPedido() != null && venta.getPedido().getUsuarioId() != null) {
            cliente = usuarioRepository.findById(venta.getPedido().getUsuarioId()).orElse(null);
        }

        model.addAttribute("venta", venta);
        model.addAttribute("cliente", cliente);

        return "comprobante";
    }

    // Endpoint para enviar o reenviar manualmente la boleta/factura por correo
    @PostMapping("/enviar-correo/{id}")
    public String enviarComprobantePorCorreo(
            @PathVariable Long id, 
            @RequestParam("email") String email,
            RedirectAttributes flash) {
        
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta != null) {
            Usuario cliente = null;
            if (venta.getPedido() != null && venta.getPedido().getUsuarioId() != null) {
                cliente = usuarioRepository.findById(venta.getPedido().getUsuarioId()).orElse(null);
            }
            emailService.enviarComprobanteEmail(email, venta, cliente);
            flash.addFlashAttribute("mensajeEmail", "¡Comprobante enviado exitosamente al correo " + email + "!");
        }
        return "redirect:/comprobante/" + id;
    }
}
