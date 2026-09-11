package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;
import com.g2.dwi_lmll.repository.PedidoRepository;
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
class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(1L);
        pedido.setTotal(new BigDecimal("120.50"));
        pedido.setEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    @DisplayName("GET /pedidos debe retornar lista con 200 OK")
    void listarTodos_debeRetornar200YLista() throws Exception {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].total").value(120.50))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(pedidoRepository).findAll();
    }

    @Test
    @DisplayName("GET /pedidos/{id} cuando existe debe retornar 200 OK")
    void buscarPorId_cuandoExiste_retorna200() throws Exception {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /pedidos/{id} cuando no existe debe retornar 404 Not Found")
    void buscarPorId_cuandoNoExiste_retorna404() throws Exception {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /pedidos debe crear pedido y retornar 201 Created")
    void crear_debeRetornar201() throws Exception {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        String json = """
                {
                    "usuarioId": 1,
                    "total": 120.50,
                    "estado": "PENDIENTE"
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /pedidos/{id} debe actualizar y retornar 200 OK")
    void actualizar_debeRetornar200() throws Exception {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        String json = """
                {
                    "usuarioId": 1,
                    "total": 150.00,
                    "estado": "PAGADO"
                }
                """;

        mockMvc.perform(put("/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /pedidos/{id} debe eliminar y retornar 204 No Content")
    void eliminar_debeRetornar204() throws Exception {
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoRepository).deleteById(1L);
    }
}
