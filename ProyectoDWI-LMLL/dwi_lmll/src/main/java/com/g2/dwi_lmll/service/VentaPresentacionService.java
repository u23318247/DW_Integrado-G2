package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.model.Venta;
import java.math.BigDecimal;

public interface VentaPresentacionService {
    
    Venta crearVentaInicialPendiente(BigDecimal total, String tipoComprobanteStr, Usuario usuario);
    
    void completarVentaExitosa(Venta venta, String metodoUsadoPayU);
    
    BigDecimal obtenerTotalIngresosReales();
    
    long obtenerCantidadPedidosPendientes();
}
