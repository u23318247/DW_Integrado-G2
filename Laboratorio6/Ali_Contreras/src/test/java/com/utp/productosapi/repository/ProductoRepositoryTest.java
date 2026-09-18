package com.utp.productosapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.utp.productosapi.model.Producto;

/**
 * Prueba de integracion de la capa de persistencia.
 * @DataJpaTest levanta solo el contexto JPA y usa una base embebida (H2) para el test.
 *
 * Nota de versiones: la guia usa Spring Boot 4.x, donde @DataJpaTest esta en
 * org.springframework.boot.data.jpa.test.autoconfigure y requiere spring-boot-starter-data-jpa-test.
 * Este proyecto usa Spring Boot 3.5.5, por lo que se importa desde
 * org.springframework.boot.test.autoconfigure.orm.jpa.
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    void debeGuardarYRecuperarProducto() {
        Producto producto = new Producto(null, "Monitor", "Tecnologia", 900.0, 5);

        Producto guardado = repository.save(producto);

        assertThat(guardado.getId()).isNotNull();
        assertThat(repository.findById(guardado.getId())).isPresent();
    }

    @Test
    void debePersistirElCampoCategoria() {
        Producto guardado = repository.save(new Producto(null, "Silla", "Muebles", 750.0, 8));

        Producto recuperado = repository.findById(guardado.getId()).orElseThrow();

        assertThat(recuperado.getCategoria()).isEqualTo("Muebles");
    }

    @Test
    void debeEliminarProducto() {
        Producto guardado = repository.save(new Producto(null, "Teclado", "Accesorios", 120.0, 10));

        repository.deleteById(guardado.getId());

        assertThat(repository.existsById(guardado.getId())).isFalse();
    }
}
