package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.model.Cupon;
import com.proyectomdweb.proyectomdweb.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class CuponInicializador implements CommandLineRunner {

    private final CuponRepository cuponRepository;

    @Override
    public void run(String... args) {
        List<Cupon> base = List.of(
                Cupon.builder()
                        .codigo("LMLL10")
                        .porcentajeDescuento(new BigDecimal("10.00"))
                        .montoMinimo(BigDecimal.ZERO)
                        .activo(true)
                        .fechaExpiracion(LocalDate.now().plusYears(1))
                        .usosMaximos(1000)
                        .usosActuales(0)
                        .build(),
                Cupon.builder()
                        .codigo("BIENVENIDO15")
                        .porcentajeDescuento(new BigDecimal("15.00"))
                        .montoMinimo(new BigDecimal("100.00"))
                        .activo(true)
                        .fechaExpiracion(LocalDate.now().plusYears(1))
                        .usosMaximos(500)
                        .usosActuales(0)
                        .build(),
                Cupon.builder()
                        .codigo("VIP20")
                        .porcentajeDescuento(new BigDecimal("20.00"))
                        .montoMinimo(new BigDecimal("250.00"))
                        .activo(true)
                        .fechaExpiracion(LocalDate.now().plusYears(1))
                        .usosMaximos(200)
                        .usosActuales(0)
                        .build()
        );

        int creados = 0;
        for (Cupon c : base) {
            if (cuponRepository.findByCodigoIgnoreCase(c.getCodigo()).isEmpty()) {
                cuponRepository.save(c);
                creados++;
            }
        }
        if (creados > 0) {
            log.info("Cupones iniciales creados: {}", creados);
        }
    }
}
