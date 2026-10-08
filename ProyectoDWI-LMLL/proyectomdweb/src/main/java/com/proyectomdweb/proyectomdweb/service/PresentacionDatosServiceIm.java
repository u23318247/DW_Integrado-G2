package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.GraficaDataDTO;
import com.proyectomdweb.proyectomdweb.model.EstadoPedido;
import com.proyectomdweb.proyectomdweb.model.Venta;
import com.proyectomdweb.proyectomdweb.repository.VentaRepository;
import com.proyectomdweb.proyectomdweb.service.PresentacionDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresentacionDatosServiceIm implements PresentacionDatosService {

    private final VentaRepository ventaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GraficaDataDTO> obtenerIngresosMensuales(int anio, int mes) {
        YearMonth ym = YearMonth.of(anio, mes);
        LocalDateTime inicio = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Venta> ventas = ventaRepository.findByPedidoEstadoAndFechaEmisionBetween(EstadoPedido.PAGADO, inicio, fin);

        Map<Integer, BigDecimal> agrupado = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getFechaEmision().getDayOfMonth(),
                        Collectors.reducing(BigDecimal.ZERO, Venta::getTotal, BigDecimal::add)
                ));

        List<GraficaDataDTO> result = new ArrayList<>();
        for (int i = 1; i <= ym.lengthOfMonth(); i++) {
            result.add(new GraficaDataDTO(String.valueOf(i), agrupado.getOrDefault(i, BigDecimal.ZERO)));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GraficaDataDTO> obtenerIngresosAnuales(int anio) {
        LocalDateTime inicio = LocalDateTime.of(anio, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(anio, 12, 31, 23, 59, 59);

        List<Venta> ventas = ventaRepository.findByPedidoEstadoAndFechaEmisionBetween(EstadoPedido.PAGADO, inicio, fin);

        Map<Integer, BigDecimal> agrupado = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getFechaEmision().getMonthValue(),
                        Collectors.reducing(BigDecimal.ZERO, Venta::getTotal, BigDecimal::add)
                ));

        List<GraficaDataDTO> result = new ArrayList<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        for (int i = 1; i <= 12; i++) {
            result.add(new GraficaDataDTO(meses[i-1], agrupado.getOrDefault(i, BigDecimal.ZERO)));
        }
        return result;
    }
}
