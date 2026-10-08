package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.*;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;
import com.proyectomdweb.proyectomdweb.service.VentaPresentacionService;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/pago")
@RequiredArgsConstructor
public class CheckoutController {

    private final VentaPresentacionService  ventaService;
    private final VentaRepository           ventaRepository;
    private final UsuarioService            usuarioService; 
    private final com.proyectomdweb.proyectomdweb.service.EmailService emailService;
    private final com.proyectomdweb.proyectomdweb.repository.UsuarioRepository usuarioRepository; 

    // Datos estáticos del Sandbox de prueba de PayU Latam
    private final String MERCHANT_ID = "508029";
    private final String API_KEY     = "4Vj8eK4rloUd272L48hsrarnUA";
    private final String ACCOUNT_ID  = "512323"; // <--- ID de pruebas para PERÚ
    private final String PAYU_URL    = "https://sandbox.checkout.payulatam.com/ppp-web-gateway-payu/"; // <--- URL de la pasarela p

    @GetMapping
    public String mostrarPaginaPago() {
        return "pago";
    }

    @PostMapping("/procesar-local")
    public String procesarLocalYPrepararPayU(
            @RequestParam("total")           BigDecimal total, 
            @RequestParam("tipoComprobante") String tipoComprobante,
            @RequestParam(value = "clienteDireccion", required = false) String clienteDireccion,
            @RequestParam(value = "clienteTelefono", required = false)  String clienteTelefono,
            Principal principal,
            Model model) {

        String  emailLogueado   = principal.getName();
        Usuario usuarioLogueado = usuarioService.obtenerUsuarioEntidadPorEmail(emailLogueado);
        
        // Si el usuario especificó dirección o teléfono en el formulario de pago, los actualizamos
        if (clienteDireccion != null && !clienteDireccion.isBlank()) {
            usuarioLogueado.setDireccion(clienteDireccion);
        }
        if (clienteTelefono != null && !clienteTelefono.isBlank()) {
            usuarioLogueado.setTelefono(clienteTelefono);
        }
        
        Venta   ventaPendiente  = ventaService.crearVentaInicialPendiente(total, tipoComprobante, usuarioLogueado);
        String  referenceCode   = "LMLL-VENTA-" + ventaPendiente.getId(); //*<--*4
        String  currency        = "PEN";                                 //*<--*4
        String  cadenaParaFirma = API_KEY + "~" + MERCHANT_ID + "~" + referenceCode + "~" + total + "~" + currency; //*<--*5
        String  signature       = generarHashMD5(cadenaParaFirma);                                                 //*<--*5

        // 6. Inyectar datos en el modelo para la página de redirección
        model.addAttribute("payuUrl", PAYU_URL);
        model.addAttribute("merchantId", MERCHANT_ID);
        model.addAttribute("accountId", ACCOUNT_ID);
        model.addAttribute("description", "Compra de ropa exclusiva en La Moda te LLama");
        model.addAttribute("referenceCode", referenceCode);
        model.addAttribute("amount", total);
        model.addAttribute("tax", "0"); 
        model.addAttribute("taxReturnBase", "0");
        model.addAttribute("currency", currency);
        model.addAttribute("signature", signature);
        model.addAttribute("buyerEmail", usuarioLogueado.getEmail());      
        model.addAttribute("buyerFullName", usuarioLogueado.getNombre());
        model.addAttribute("test", "1"); 
        model.addAttribute("responseUrl", "http://localhost:8080/pago/respuesta");

        return "payu-redirect"; 
    }

    @GetMapping("/respuesta")
    public String capturarRespuestaPayU(@RequestParam Map<String, String> parametrosPayU, Model model) {
        try {
            // 1. Extrae los datos clave del diccionario de PayU
            String referenceCode = parametrosPayU.get("referenceCode");
            String transactionState = parametrosPayU.get("transactionState");
            
            // Prioriza leer el nombre legible de la tarjeta, si no viene, se lee el genérico
            String metodoUsado = parametrosPayU.getOrDefault("lapPaymentMethod", 
                                 parametrosPayU.getOrDefault("polPaymentMethodType", "Desconocido"));

            if (referenceCode == null) {
                return "redirect:/"; // Seguridad: Si alguien entra a la URL manualmente, lo bota a inicio
            }

            // 2. Limpia el ID y buscar la venta
            Long ventaId = Long.parseLong(referenceCode.replace("LMLL-VENTA-", "").trim());
            Venta venta = ventaRepository.findById(ventaId)
                    .orElseThrow(() -> new RuntimeException("No se encontró la venta en la base de datos"));

            // 3. Procesa el estado (PayU envía "4" cuando la tarjeta fue Aprobada)
            if ("4".equals(transactionState)) {
                
                // Evita actualizar doble si el usuario recarga la página
                if (venta.getPedido().getEstado() != EstadoPedido.PAGADO) {
                    ventaService.completarVentaExitosa(venta, metodoUsado);
                    
                    // Enviar comprobante (Factura / Boleta) al correo del cliente
                    try {
                        Usuario cliente = usuarioService.obtenerUsuarioEntidadPorEmail(parametrosPayU.getOrDefault("buyerEmail", ""));
                        emailService.enviarComprobanteEmail(cliente.getEmail(), venta, cliente);
                    } catch (Exception ex) {
                        // Si el email no se encuentra por parámetro, buscamos por el ID del usuario en el pedido
                        if (venta.getPedido() != null && venta.getPedido().getUsuarioId() != null) {
                            var clienteOpt = usuarioRepository.findById(venta.getPedido().getUsuarioId());
                            clienteOpt.ifPresent(c -> emailService.enviarComprobanteEmail(c.getEmail(), venta, c));
                        }
                    }
                }
                
                model.addAttribute("status", "EXITOSO");
                model.addAttribute("mensaje", "Tu pago fue procesado con éxito. ¡Gracias por tu compra!");
            } else {
                model.addAttribute("status", "RECHAZADO");
                model.addAttribute("mensaje", "La transacción fue rechazada o se encuentra pendiente.");
            }

            model.addAttribute("venta", venta);
            return "pago-resultado";

        } catch (Exception e) {
            // Si cualquier cosa falla (ej. el ID fue manipulado), atrapa el error 
            System.out.println("Error procesando callback de PayU: " + e.getMessage());
            model.addAttribute("status", "ERROR");
            model.addAttribute("mensaje", "Hubo un problema verificando tu pago, pero no te preocupes. Si se hizo un cobro, contáctanos.");
            return "pago-resultado";
        }
    }

    // Utilidad estándar de Java para cálculo de firmas de seguridad hash
    private String generarHashMD5(String cadena) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(cadena.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}