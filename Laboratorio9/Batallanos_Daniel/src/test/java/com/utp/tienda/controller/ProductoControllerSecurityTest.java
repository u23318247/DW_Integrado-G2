package com.utp.tienda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.dto.ProductoRequest;
import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.model.Producto;
import com.utp.tienda.security.CustomUserDetailsService;
import com.utp.tienda.service.ProductoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
 * Tarea 8 - Pruebas de seguridad del controlador de productos.
 *
 * <p>Slice {@code @WebMvcTest}: carga solo la capa web (controlador, manejo de
 * excepciones y seguridad) con el servicio simulado. Se importa
 * {@link SecurityConfig} porque el slice no la detecta automaticamente, de modo que
 * se prueban las reglas REALES del {@code SecurityFilterChain} y no la
 * configuracion por defecto de Spring Boot.</p>
 */
@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)
class ProductoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    /** Requerido por el DaoAuthenticationProvider declarado en SecurityConfig. */
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String JSON_NUEVO_PRODUCTO = """
            {
              "nombre": "Teclado mecanico",
              "precio": 249.90,
              "stock": 15,
              "descripcion": "Teclado RGB"
            }
            """;

    private Producto productoEjemplo() {
        Producto producto = new Producto("Teclado mecanico", new BigDecimal("249.90"), 15, "Teclado RGB");
        producto.setId(1L);
        return producto;
    }

    // =========================================================
    // Escenario 1 (enunciado): GET anonimo -> 401 Unauthorized
    // =========================================================
    @Test
    @DisplayName("1) GET /api/productos anonimo responde 401 Unauthorized")
    void getProductosAnonimoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());

        verify(productoService, never()).listarTodos();
    }

    // =========================================================
    // Escenario 2 (enunciado): GET con rol USER -> 200 OK
    // =========================================================
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("2) GET /api/productos con rol USER responde 200 OK")
    void getProductosComoUserDevuelve200() throws Exception {
        given(productoService.listarTodos()).willReturn(List.of(productoEjemplo()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado mecanico"));
    }

    // =========================================================
    // Escenario 3 (enunciado): DELETE con rol USER -> 403 Forbidden
    // =========================================================
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("3) DELETE /api/productos/1 con rol USER responde 403 Forbidden")
    void deleteProductoComoUserDevuelve403() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());

        verify(productoService, never()).eliminarProducto(anyLong());
    }

    // =========================================================
    // Escenarios adicionales de la matriz de autorizacion
    // =========================================================

    @Test
    @DisplayName("4) POST /api/productos anonimo responde 401 Unauthorized")
    void postProductoAnonimoDevuelve401() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isUnauthorized());

        verify(productoService, never()).crear(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("5) POST /api/productos con rol USER responde 403 Forbidden")
    void postProductoComoUserDevuelve403() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isForbidden());

        verify(productoService, never()).crear(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6) POST /api/productos con rol ADMIN responde 201 Created")
    void postProductoComoAdminDevuelve201() throws Exception {
        ProductoRequest request = new ProductoRequest("Teclado mecanico",
                new BigDecimal("249.90"), 15, "Teclado RGB");
        given(productoService.crear(request)).willReturn(productoEjemplo());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Teclado mecanico"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("7) PUT /api/productos/1 con rol USER responde 403 Forbidden")
    void putProductoComoUserDevuelve403() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("8) PUT /api/productos/1 con rol ADMIN responde 200 OK")
    void putProductoComoAdminDevuelve200() throws Exception {
        ProductoRequest request = new ProductoRequest("Teclado mecanico",
                new BigDecimal("249.90"), 15, "Teclado RGB");
        given(productoService.actualizar(eq(1L), any(ProductoRequest.class)))
                .willReturn(productoEjemplo());

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("9) PATCH /api/productos/1 con rol USER responde 403 Forbidden")
    void patchProductoComoUserDevuelve403() throws Exception {
        mockMvc.perform(patch("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stock\": 5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("10) PATCH /api/productos/1 con rol ADMIN responde 200 OK")
    void patchProductoComoAdminDevuelve200() throws Exception {
        given(productoService.actualizarParcial(eq(1L), any()))
                .willReturn(productoEjemplo());

        mockMvc.perform(patch("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stock\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("11) DELETE /api/productos/1 con rol ADMIN responde 204 No Content")
    void deleteProductoComoAdminDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminarProducto(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("12) GET /api/productos/999 inexistente responde 404 Not Found")
    void getProductoInexistenteDevuelve404() throws Exception {
        given(productoService.buscarPorId(999L))
                .willThrow(new RecursoNoEncontradoException("Producto no encontrado con id: 999"));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "INVITADO")
    @DisplayName("13) Un rol no contemplado (INVITADO) recibe 403 al intentar escribir")
    void usuarioConRolNoContempladoNoPuedeEscribir() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_NUEVO_PRODUCTO))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());

        verify(productoService, never()).crear(any());
        verify(productoService, never()).eliminarProducto(anyLong());
    }

    @Test
    @WithMockUser(roles = "INVITADO")
    @DisplayName("14) Un rol no contemplado tambien recibe 403 al leer productos")
    void usuarioConRolNoContempladoNoPuedeLeer() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isForbidden());

        verify(productoService, never()).listarTodos();
    }
}