package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.model.*;
import com.proyectomdweb.proyectomdweb.repository.PedidoRepository;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import com.proyectomdweb.proyectomdweb.service.VentaPresentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VentaPresentacionServiceIm implements VentaPresentacionService {

    private final PedidoRepository pedidoRepository;
    private final VentaRepository  ventaRepository;

    @Override
    @Transactional
    public Venta crearVentaInicialPendiente(BigDecimal total, String tipoComprobanteStr, Usuario usuario) {
        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setUsuarioId(usuario.getId()); 
        pedidoNuevo.setTotal(total);
        pedidoNuevo.setEstado(EstadoPedido.PENDIENTE); 
        
        // Generar PIN de entrega seguro de 4 dígitos (ej. 7482)
        int pinAleatorio = 1000 + new Random().nextInt(9000);
        pedidoNuevo.setCodigoEntrega(String.valueOf(pinAleatorio));
        pedidoNuevo.setDireccionEnvio(usuario.getDireccion() != null && !usuario.getDireccion().isBlank() ? usuario.getDireccion() : "Dirección indicada por el cliente");
        pedidoNuevo.setTelefonoContacto(usuario.getTelefono() != null && !usuario.getTelefono().isBlank() ? usuario.getTelefono() : "Sin teléfono");
        
        pedidoNuevo = pedidoRepository.save(pedidoNuevo);

        BigDecimal divisorIgv = new BigDecimal("1.18");
        BigDecimal subtotal   = total.divide(divisorIgv, 2, RoundingMode.HALF_UP);
        BigDecimal igv        = total.subtract(subtotal);

        Random random           = new Random();
        int    correlativo      = random.nextInt(999999);
        String prefijo          = tipoComprobanteStr.equalsIgnoreCase("FACTURA") ? "F001-" : "B001-";
        String serieCorrelativo = String.format("%s%06d", prefijo, correlativo);

        Venta venta = new Venta();
        venta.setPedido(pedidoNuevo);
        venta.setTipoComprobante(TipoComprobante.valueOf(tipoComprobanteStr.toUpperCase()));
        venta.setSerieCorrelativo(serieCorrelativo);
        venta.setMetodoPago(MetodoPago.TARJETA_CREDITO); 
        venta.setFechaEmision(LocalDateTime.now());
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(total);

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void completarVentaExitosa(Venta venta, String metodoUsadoPayU) {
        Pedido pedido = venta.getPedido();
        pedido.setEstado(EstadoPedido.PAGADO);
        pedidoRepository.save(pedido);

        MetodoPago metodoFinal = MetodoPago.TARJETA_CREDITO; 
        
        if (metodoUsadoPayU != null) {
            String metodoStr = metodoUsadoPayU.toLowerCase();
            if (metodoStr.contains("yape") || metodoStr.contains("plin")) {
                metodoFinal = MetodoPago.YAPE;
            } else if (metodoStr.contains("cash") || metodoStr.contains("efectivo")) {
                metodoFinal = MetodoPago.EFECTIVO;
            } else if (metodoStr.contains("paypal")) {
                metodoFinal = MetodoPago.PAYPAL;
            }
        }

        venta.setMetodoPago(metodoFinal);
        venta.setFechaEmision(LocalDateTime.now()); 
        ventaRepository.save(venta);
    }

    // IMPLEMENTACIÓN DE LAS ESTADÍSTICAS REALES
    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalIngresosReales() {
        return ventaRepository.sumarTotalIngresosPorEstado(EstadoPedido.PAGADO);
    }

    @Override
    @Transactional(readOnly = true)
    public long obtenerCantidadPedidosPendientes() {
        return pedidoRepository.countByEstado(EstadoPedido.PENDIENTE);
    }
}
