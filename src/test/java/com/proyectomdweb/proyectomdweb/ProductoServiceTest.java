package com.proyectomdweb.proyectomdweb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.proyectomdweb.proyectomdweb.dto.ActualizarStockRequest;
import com.proyectomdweb.proyectomdweb.dto.ProductoRequest;
import com.proyectomdweb.proyectomdweb.exception.ResourceNotFoundException;
import com.proyectomdweb.proyectomdweb.exception.StockInsuficienteException;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.service.ProductoService;

class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    @Test
    void debeListarPrendasIniciales() {
        List<Producto> lista = service.listar(null, null);
        assertThat(lista).isNotEmpty();
    }

    @Test
    void debeFiltrarPorCategoriaYGenero() {
        List<Producto> polosUnisex = service.listar("Polos", "Unisex");
        assertThat(polosUnisex).isNotEmpty();
        assertThat(polosUnisex.get(0).getCategoria()).isEqualTo("Polos");
        assertThat(polosUnisex.get(0).getGenero()).isEqualTo("Unisex");
    }

    @Test
    void debeCrearPrendaCorrectamente() {
        ProductoRequest req = new ProductoRequest("Casaca Jean", "Casacas", "Unisex", "/img/jean.jpg", 110.0, 10);
        Producto creado = service.crear(req);

        assertThat(creado.getId()).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("Casaca Jean");
        assertThat(creado.getDisponible()).isTrue();
    }

    @Test
    void debeDisminuirStockCorrectamente() {
        Producto p = service.buscarPorId(1L);
        int stockInicial = p.getStock();

        Producto actualizado = service.disminuirStock(1L, 5);
        assertThat(actualizado.getStock()).isEqualTo(stockInicial - 5);
    }

    @Test
    void debeLanzarExcepcionSiStockInsuficiente() {
        assertThatThrownBy(() -> service.disminuirStock(1L, 9999))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    void debeLanzarExcepcionAlBuscarIdInexistente() {
        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
