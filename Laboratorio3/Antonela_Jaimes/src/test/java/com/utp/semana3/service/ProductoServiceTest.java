package com.utp.semana3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.utp.semana3.model.Producto;

class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    @Test
    void registrarProductoValido_debeAsignarIdYGuardar() {
        Producto producto = new Producto(null, "Laptop", 3500.00, 10);

        Producto registrado = service.registrar(producto);

        assertThat(registrado.getId()).isNotNull();
        assertThat(registrado.getNombre()).isEqualTo("Laptop");
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void registrarProductoConPrecioCero_debeLanzarExcepcion() {
        Producto producto = new Producto(null, "Mouse", 0.00, 5);

        assertThatThrownBy(() -> service.registrar(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El precio debe ser mayor que cero");
    }

    @Test
    void registrarProductoSinNombre_debeLanzarExcepcion() {
        Producto producto = new Producto(null, "", 100.00, 5);

        assertThatThrownBy(() -> service.registrar(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre es obligatorio");
    }

    @Test
    void registrarProductoConStockNegativo_debeLanzarExcepcion() {
        Producto producto = new Producto(null, "Teclado", 150.00, -1);

        assertThatThrownBy(() -> service.registrar(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El stock no puede ser negativo");
    }

    @Test
    void buscarPorId_debeRetornarProductoCuandoExiste() {
        Producto producto = service.registrar(new Producto(null, "Monitor", 600.00, 8));

        assertThat(service.buscarPorId(producto.getId())).isPresent();
    }

    @Test
    void eliminar_debeRetornarTrueCuandoExiste() {
        Producto producto = service.registrar(new Producto(null, "Auriculares", 80.00, 15));

        boolean eliminado = service.eliminar(producto.getId());

        assertThat(eliminado).isTrue();
        assertThat(service.buscarPorId(producto.getId())).isEmpty();
    }
}
