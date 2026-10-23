package com.utp.tienda.service;

import com.utp.tienda.dto.ResumenBackendResponse;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminResumenService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public AdminResumenService(ProductoRepository productoRepository,
                               MovimientoStockRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public ResumenBackendResponse obtenerResumen() {
        return new ResumenBackendResponse(
                productoRepository.count(),
                productoRepository.countByStockLessThan(5),
                movimientoRepository.count(),
                "BACKEND_OPERATIVO");
    }
}
