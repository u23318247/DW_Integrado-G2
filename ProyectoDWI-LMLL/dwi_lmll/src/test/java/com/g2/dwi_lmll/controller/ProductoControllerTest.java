package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.service.CategoriaService;
import com.g2.dwi_lmll.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoController productoController;

    private ProductoDTO productoDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setViewResolvers(viewResolver)
                .build();

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Polos");

        productoDTO = new ProductoDTO(
                1L,
                "Polo Oversize",
                "Unisex",
                "/img/polo.jpg",
                new BigDecimal("49.90"),
                true,
                1L,
                "Polos"
        );
    }

    @Test
    @DisplayName("GET /productos debe retornar la vista 'productos' con categorías y productos")
    void listarTodosLosProductos_debeRetornarVistaProductos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoDTO));
        when(categoriaService.listarTodas()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("productos"));

        verify(productoService).listarTodos();
        verify(categoriaService).listarTodas();
    }

    @Test
    @DisplayName("GET /productos/ajax debe retornar el fragmento 'productos :: lista-productos'")
    void filtrarAjax_debeRetornarFragmentoProductos() throws Exception {
        when(productoService.filtrar("Unisex", 1L)).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/productos/ajax")
                        .param("genero", "Unisex")
                        .param("categoriaId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos :: lista-productos"))
                .andExpect(model().attributeExists("productos"));

        verify(productoService).filtrar("Unisex", 1L);
    }
}
