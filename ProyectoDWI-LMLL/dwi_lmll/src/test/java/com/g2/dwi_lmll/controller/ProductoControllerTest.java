package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();

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
    @DisplayName("GET /api/productos debe retornar lista de productos con status 200")
    void listarTodosLosProductos_debeRetornar200YLista() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Polo Oversize"))
                .andExpect(jsonPath("$[0].precioBase").value(49.90));

        verify(productoService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/productos/{id} debe retornar producto con status 200")
    void buscarPorId_debeRetornar200YProducto() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(productoDTO));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Polo Oversize"));

        verify(productoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("POST /api/productos debe crear producto y retornar status 201")
    void crear_debeRetornar201() throws Exception {
        when(productoService.guardar(any(ProductoDTO.class))).thenReturn(productoDTO);

        String json = """
                {
                    "nombre": "Polo Oversize",
                    "genero": "Unisex",
                    "imagenUrl": "/img/polo.jpg",
                    "precioBase": 49.90,
                    "disponibilidad": true,
                    "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/productos/1"))
                .andExpect(jsonPath("$.nombre").value("Polo Oversize"));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} debe eliminar y retornar status 204")
    void eliminar_debeRetornar204() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(productoDTO));

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }
}
