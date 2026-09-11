package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.exception.GlobalExceptionHandler;
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
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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
    @DisplayName("GET /api/productos debe retornar lista de productos con 200 OK")
    void listarTodos_debeRetornar200YLista() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Polo Oversize"))
                .andExpect(jsonPath("$[0].categoriaNombre").value("Polos"));

        verify(productoService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/productos?texto= debe buscar por nombre")
    void listar_conTexto_debeBuscarPorNombre() throws Exception {
        when(productoService.buscarPorNombre("polo")).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/api/productos").param("texto", "polo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Polo Oversize"));

        verify(productoService).buscarPorNombre("polo");
        verify(productoService, never()).listarTodos();
    }

    @Test
    @DisplayName("GET /api/productos/{id} cuando existe debe retornar 200 OK")
    void buscarPorId_cuandoExiste_retorna200() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(productoDTO));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioBase").value(49.90));
    }

    @Test
    @DisplayName("GET /api/productos/{id} cuando no existe debe retornar 404 Not Found")
    void buscarPorId_cuandoNoExiste_retorna404() throws Exception {
        when(productoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("No existe un producto con id: 99"));
    }

    @Test
    @DisplayName("POST /api/productos debe crear producto y retornar 201 Created con Location")
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
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/productos con datos invalidos debe retornar 400 Bad Request")
    void crear_datosInvalidos_debeRetornar400() throws Exception {
        String json = """
                {
                    "nombre": "",
                    "genero": "Unisex",
                    "precioBase": -5,
                    "disponibilidad": true
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));

        verify(productoService, never()).guardar(any());
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} debe eliminar y retornar 204 No Content")
    void eliminar_debeRetornar204() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(productoDTO));
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }
}
