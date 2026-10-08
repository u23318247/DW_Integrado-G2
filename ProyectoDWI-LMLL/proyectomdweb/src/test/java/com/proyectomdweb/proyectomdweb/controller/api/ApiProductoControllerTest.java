package com.proyectomdweb.proyectomdweb.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.dtos.ProductoRequest;
import com.proyectomdweb.proyectomdweb.exception.RestExceptionHandler;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApiProductoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ApiProductoController apiProductoController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(apiProductoController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("MockMvc GET /api/productos debe retornar 200 OK y lista JSON")
    void debeListarProductos() throws Exception {
        ProductoDTO dto = new ProductoDTO(1L, "Polera Urbana", "Unisex", "polera.png", new BigDecimal("120.00"), true, 1L, "Poleras");
        when(productoService.listarTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Polera Urbana"));
    }

    @Test
    @DisplayName("MockMvc GET /api/productos/{id} debe retornar 200 OK cuando existe")
    void debeObtenerProductoPorId() throws Exception {
        ProductoDTO dto = new ProductoDTO(1L, "Polera Urbana", "Unisex", "polera.png", new BigDecimal("120.00"), true, 1L, "Poleras");
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Polera Urbana"));
    }

    @Test
    @DisplayName("MockMvc GET /api/productos/{id} debe retornar 404 Not Found cuando no existe")
    void debeRetornar404SiNoExiste() throws Exception {
        when(productoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("MockMvc POST /api/productos debe retornar 201 Created y header Location")
    void debeCrearProductoConLocationHeader() throws Exception {
        ProductoRequest request = new ProductoRequest(
                "Gorra New Era",
                "Unisex",
                new BigDecimal("89.90"),
                true,
                2L,
                "gorra.png"
        );
        ProductoDTO guardado = new ProductoDTO(10L, "Gorra New Era", "Unisex", "gorra.png", new BigDecimal("89.90"), true, 2L, "Accesorios");

        when(productoService.guardar(any(ProductoDTO.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Gorra New Era"));
    }

    @Test
    @DisplayName("MockMvc PATCH /api/productos/{id}/disponibilidad debe retornar 200 OK")
    void debeActualizarDisponibilidadConPatch() throws Exception {
        ProductoDTO existente = new ProductoDTO(1L, "Polo", "Unisex", "p.png", new BigDecimal("50.00"), true, 1L, "Polos");
        ProductoDTO actualizado = new ProductoDTO(1L, "Polo", "Unisex", "p.png", new BigDecimal("50.00"), false, 1L, "Polos");

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(productoService.guardar(any(ProductoDTO.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/productos/1/disponibilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("disponibilidad", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibilidad").value(false));
    }

    @Test
    @DisplayName("MockMvc DELETE /api/productos/{id} debe retornar 204 No Content")
    void debeEliminarProducto() throws Exception {
        ProductoDTO existente = new ProductoDTO(1L, "Polo", "Unisex", "p.png", new BigDecimal("50.00"), true, 1L, "Polos");
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(existente));

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
