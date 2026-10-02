package com.utp.tienda.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.utp.tienda.model.Producto;

/**
 * Verifica las consultas derivadas y JPQL del repositorio.
 * Spring Boot 3.5.x: @DataJpaTest se importa de org.springframework.boot.test.autoconfigure.orm.jpa.
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @BeforeEach
    void setUp() {
        repository.saveAll(List.of(
                new Producto("Laptop Lenovo ThinkPad", "Tecnologia", new BigDecimal("4200.00"), 8),
                new Producto("Mouse Logitech MX", "Tecnologia", new BigDecimal("320.00"), 20),
                new Producto("Silla ergonomica", "Muebles", new BigDecimal("850.00"), 6),
                new Producto("Escritorio ejecutivo", "Muebles", new BigDecimal("1200.00"), 4),
                new Producto("Monitor 27 pulgadas", "Tecnologia", new BigDecimal("1450.00"), 10)));
    }

    @Test
    void buscarPorNombreEsParcialEIgnoraMayusculas() {
        List<Producto> resultado = repository.buscarPorNombre("LAP");

        assertThat(resultado).extracting(Producto::getNombre)
                .containsExactly("Laptop Lenovo ThinkPad");
    }

    @Test
    void consultaDerivadaPorCategoriaIgnoraMayusculas() {
        assertThat(repository.findByCategoriaIgnoreCase("tecnologia")).hasSize(3);
    }

    @Test
    void rangoDePrecioOrdenaDeMenorAMayor() {
        List<Producto> resultado = repository.buscarPorRangoPrecio(
                new BigDecimal("300"), new BigDecimal("1500"));

        assertThat(resultado).extracting(Producto::getNombre).containsExactly(
                "Mouse Logitech MX", "Silla ergonomica", "Escritorio ejecutivo", "Monitor 27 pulgadas");
    }

    @Test
    void stockBajoDevuelveProductosBajoElLimite() {
        List<Producto> resultado = repository.buscarConStockBajo(5);

        assertThat(resultado).extracting(Producto::getNombre).containsExactly("Escritorio ejecutivo");
    }
}
