package com.utp.tienda.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.utp.tienda.model.Producto;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void debeBuscarPorNombreYPorRangoPrecio() {
        Producto p1 = new Producto("Laptop Lenovo ThinkPad", "Tecnologia", new BigDecimal("4200.00"), 8);
        Producto p2 = new Producto("Mouse Logitech MX", "Tecnologia", new BigDecimal("320.00"), 20);
        productoRepository.save(p1);
        productoRepository.save(p2);

        List<Producto> encontrados = productoRepository.buscarPorNombre("ThinkPad");
        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNombre()).isEqualTo("Laptop Lenovo ThinkPad");

        List<Producto> porRango = productoRepository.buscarPorRangoPrecio(new BigDecimal("300.00"), new BigDecimal("500.00"));
        assertThat(porRango).hasSize(1);
        assertThat(porRango.get(0).getNombre()).isEqualTo("Mouse Logitech MX");
    }
}
