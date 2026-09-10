package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.model.Venta;
import com.g2.dwi_lmll.repository.VentaRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaController ventaController;

    private Venta venta;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();

        venta = new Venta();
        venta.setId(1L);
        venta.setSerieCorrelativo("B001-000001");
        venta.setTotal(new BigDecimal("150.00"));
        venta.setSubtotal(new BigDecimal("127.12"));
        venta.setIgv(new BigDecimal("22.88"));
    }

    @Test
    @DisplayName("GET /api/ventas debe retornar lista con 200 OK")
    void listar_debeRetornar200YLista() throws Exception {
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serieCorrelativo").value("B001-000001"));

        verify(ventaRepository).findAll();
    }

    @Test
    @DisplayName("GET /api/ventas/{id} cuando existe debe retornar 200 OK")
    void buscar_cuandoExiste_retorna200() throws Exception {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serieCorrelativo").value("B001-000001"));
    }

    @Test
    @DisplayName("GET /api/ventas/{id} cuando no existe debe retornar 404 Not Found")
    void buscar_cuandoNoExiste_retorna404() throws Exception {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/ventas debe guardar venta y retornar 201 Created")
    void crear_debeRetornar201() throws Exception {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        String json = """
                {
                    "serieCorrelativo": "B001-000001",
                    "total": 150.00,
                    "subtotal": 127.12,
                    "igv": 22.88
                }
                """;

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serieCorrelativo").value("B001-000001"));
    }
}
