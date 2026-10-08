package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.mapper.ProductoMapper;
import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.repository.CategoriaRepository;
import com.proyectomdweb.proyectomdweb.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceIm productoService;

    private Producto producto;
    private ProductoDTO productoDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Polos");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Polo Oversize");
        producto.setGenero("Unisex");
        producto.setImagenUrl("polo.png");
        producto.setPrecioBase(new BigDecimal("79.90"));
        producto.setDisponibilidad(true);
        producto.setCategoria(categoria);

        productoDTO = new ProductoDTO(
                1L,
                "Polo Oversize",
                "Unisex",
                "polo.png",
                new BigDecimal("79.90"),
                true,
                1L,
                "Polos"
        );
    }

    @Test
    @DisplayName("TDD: Debe listar todos los productos correctamente")
    void debeListarTodosLosProductos() {
        when(productoRepository.findAllConCategoria()).thenReturn(List.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = productoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Polo Oversize", resultado.get(0).nombre());
        verify(productoRepository, times(1)).findAllConCategoria();
    }

    @Test
    @DisplayName("TDD: Debe buscar un producto por ID existente")
    void debeBuscarProductoPorIdExistente() {
        when(productoRepository.findByIdConCategoria(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        Optional<ProductoDTO> resultado = productoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Polo Oversize", resultado.get().nombre());
    }

    @Test
    @DisplayName("TDD: Debe retornar vacío si el producto no existe")
    void debeRetornarVacioSiProductoNoExiste() {
        when(productoRepository.findByIdConCategoria(99L)).thenReturn(Optional.empty());

        Optional<ProductoDTO> resultado = productoService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("TDD: Debe lanzar excepción si el nombre es nulo o vacío")
    void debeRechazarProductoConNombreInvalido() {
        ProductoDTO dtoInvalido = new ProductoDTO(null, "", "Unisex", "polo.png", new BigDecimal("50.00"), true, 1L, null);

        assertThrows(IllegalArgumentException.class, () -> productoService.guardar(dtoInvalido));
    }

    @Test
    @DisplayName("TDD: Debe lanzar excepción si el precio es menor o igual a cero")
    void debeRechazarProductoConPrecioCeroONegativo() {
        ProductoDTO dtoInvalido = new ProductoDTO(null, "Camisa", "Unisex", "camisa.png", BigDecimal.ZERO, true, 1L, null);

        assertThrows(IllegalArgumentException.class, () -> productoService.guardar(dtoInvalido));
    }

    @Test
    @DisplayName("TDD: Debe eliminar un producto existente")
    void debeEliminarProductoExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.eliminar(1L));
        verify(productoRepository, times(1)).deleteById(1L);
    }
}
