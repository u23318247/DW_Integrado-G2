package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.model.Venta;
import com.g2.dwi_lmll.model.enums.MetodoPago;
import com.g2.dwi_lmll.model.enums.TipoComprobante;
import com.g2.dwi_lmll.repository.PedidoRepository;
import com.g2.dwi_lmll.repository.VentaRepository;
import com.g2.dwi_lmll.service.VentaPresentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class VentaPresentacionServiceImpl implements VentaPresentacionService {
    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;

    public Venta crearVentaInicialPendiente(BigDecimal total, String tipo, Usuario usuario){
        Venta venta = new Venta();
        venta.setTotal(total);
        venta.setSubtotal(total);
        venta.setIgv(BigDecimal.ZERO);
        venta.setTipoComprobante(TipoComprobante.valueOf(tipo));
        return ventaRepository.save(venta);
    }
    public void completarVentaExitosa(Venta venta,String metodo){
        venta.setMetodoPago(MetodoPago.valueOf(metodo));
        ventaRepository.save(venta);
    }
    public BigDecimal obtenerTotalIngresosReales(){ return ventaRepository.sumarTotalIngresosPorEstado(com.g2.dwi_lmll.model.EstadoPedido.PAGADO); }
    public long obtenerCantidadPedidosPendientes(){ return pedidoRepository.countByEstado(com.g2.dwi_lmll.model.EstadoPedido.PENDIENTE); }
}
