package com.utp.tienda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.tienda.config.JwtConfig;
import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;
import com.utp.tienda.service.ReporteService;

/**
 * Prueba de autorizacion de la capa web. El Service es un mock: solo interesa
 * comprobar que la cadena de filtros responde 401 / 403 / 2xx segun el rol.
 *
 * Semana 9: la configuracion es JWT (Resource Server); @WithMockUser coloca directamente la
 * autenticacion en el contexto, por lo que las reglas por rol se prueban igual que en la semana 8.
 * Spring Boot 3.5.x: @WebMvcTest esta en org.springframework.boot.test.autoconfigure.web.servlet.
 */
@WebMvcTest({ProductoController.class, InventarioController.class, PublicoController.class, AdminController.class})
@Import({SecurityConfig.class, JwtConfig.class})
class ProductoControllerSecurityTest {

    private static final String PRODUCTO_JSON = """
            {"nombre":"Monitor 27 pulgadas","categoria":"Tecnologia","precio":1250.00,"stock":8}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private ReporteService reporteService;

    // Lo necesita el AuthenticationManager de SecurityConfig; en este slice no se usa el login.
    @MockitoBean
    private UserDetailsService userDetailsService;

    // ---------- Escenarios de la guia ----------

    @Test
    void estadoPublicoSinAutenticacion_debeResponder200() throws Exception {
        mockMvc.perform(get("/api/publico/estado"))
                .andExpect(status().isOk());
    }

    @Test
    void listarSinAutenticacion_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarComoUser_debeResponder200() throws Exception {
        when(productoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void crearComoUser_debeResponder403() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void eliminarComoUser_debeResponder403() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearComoAdmin_debeResponder201() throws Exception {
        when(productoService.crear(any(Producto.class))).thenReturn(
                new Producto("Monitor 27 pulgadas", "Tecnologia", new BigDecimal("1250.00"), 8));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarComoAdmin_debeResponder204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    // ---------- Actividad de consolidacion: inventario ----------

    @Test
    void inventarioSinAutenticacion_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void inventarioLecturaComoUser_debeResponder200() throws Exception {
        when(productoService.buscarConStockBajo(5)).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void ajusteComoUser_debeResponder403() throws Exception {
        mockMvc.perform(post("/api/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productoId":1,"tipo":"ENTRADA","cantidad":3}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ajusteComoAdmin_debeResponder200() throws Exception {
        when(productoService.registrarEntrada(1L, 3)).thenReturn(
                new Producto("Laptop", "Tecnologia", new BigDecimal("4200.00"), 11));

        mockMvc.perform(post("/api/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productoId":1,"tipo":"ENTRADA","cantidad":3}
                                """))
                .andExpect(status().isOk());
    }

    // ---------- Reto de aplicacion: /api/admin/reporte ----------

    @Test
    void reporteSinToken_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void reporteComoUser_debeResponder403() throws Exception {
        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reporteComoAdmin_debeResponder200() throws Exception {
        when(reporteService.generarReporte()).thenReturn(java.util.Map.of("totalProductos", 5L));

        mockMvc.perform(get("/api/admin/reporte"))
                .andExpect(status().isOk());
    }
}
