package com.utp.tienda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.dto.AjusteInventarioRequest;
import com.utp.tienda.model.MovimientoStock;
import com.utp.tienda.model.Producto;
import com.utp.tienda.model.TipoMovimiento;
import com.utp.tienda.security.CustomUserDetailsService;
import com.utp.tienda.service.InventarioService;
import java.math.BigDecimal;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tarea 9.1 - Pruebas de seguridad del modulo de inventario (consolidacion).
 *
 * <p>Verifica la matriz: GET para USER/ADMIN y POST /api/inventario/ajustes
 * exclusivo de ADMIN.</p>
 */
@WebMvcTest(InventarioController.class)
@Import(SecurityConfig.class)
class InventarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String JSON_AJUSTE = """
            {
              "productoId": 1,
              "tipo": "AJUSTE_NEGATIVO",
              "cantidad": 3,
              "motivo": "Producto danado"
            }
            """;

    private MovimientoStock movimientoEjemplo() {
        Producto producto = new Producto("Teclado mecanico", new BigDecimal("249.90"), 15);
        producto.setId(1L);
        MovimientoStock movimiento = new MovimientoStock(producto, TipoMovimiento.AJUSTE_NEGATIVO,
                3, "Producto danado", "admin");
        movimiento.setId(10L);
        return movimiento;
    }

    @Test
    @DisplayName("1) GET /api/inventario/movimientos anonimo responde 401")
    void listarMovimientosAnonimoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/inventario/movimientos"))
                .andExpect(status().isUnauthorized());

        verify(inventarioService, never()).listarMovimientos();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("2) GET /api/inventario/movimientos con USER responde 200")
    void listarMovimientosComoUserDevuelve200() throws Exception {
        given(inventarioService.listarMovimientos()).willReturn(List.of(movimientoEjemplo()));

        mockMvc.perform(get("/api/inventario/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("AJUSTE_NEGATIVO"))
                .andExpect(jsonPath("$[0].usuario").value("admin"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("3) GET /api/inventario/stock/1 con USER responde 200")
    void consultarStockComoUserDevuelve200() throws Exception {
        Producto producto = new Producto("Teclado mecanico", new BigDecimal("249.90"), 15);
        producto.setId(1L);
        given(inventarioService.consultarStock(1L)).willReturn(producto);

        mockMvc.perform(get("/api/inventario/stock/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    @DisplayName("4) POST /api/inventario/ajustes anonimo responde 401")
    void registrarAjusteAnonimoDevuelve401() throws Exception {
        mockMvc.perform(post("/api/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AJUSTE))
                .andExpect(status().isUnauthorized());

        verify(inventarioService, never()).registrarAjuste(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("5) POST /api/inventario/ajustes con USER responde 403")
    void registrarAjusteComoUserDevuelve403() throws Exception {
        mockMvc.perform(post("/api/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AJUSTE))
                .andExpect(status().isForbidden());

        verify(inventarioService, never()).registrarAjuste(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6) POST /api/inventario/ajustes con ADMIN responde 201")
    void registrarAjusteComoAdminDevuelve201() throws Exception {
        given(inventarioService.registrarAjuste(any(AjusteInventarioRequest.class)))
                .willReturn(movimientoEjemplo());

        mockMvc.perform(post("/api/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_AJUSTE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("AJUSTE_NEGATIVO"))
                .andExpect(jsonPath("$.cantidad").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("7) Los movimientos por producto solo los consultan USER y ADMIN")
    void listarMovimientosPorProductoConAdminDevuelve200() throws Exception {
        given(inventarioService.listarMovimientosPorProducto(anyLong()))
                .willReturn(List.of(movimientoEjemplo()));

        mockMvc.perform(get("/api/inventario/movimientos/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }
}