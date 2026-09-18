package com.utp.productosapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.service.ProductoService;

/**
 * Prueba de la capa web con MockMvc. El Service real se sustituye por un mock
 * (@MockitoBean), de modo que solo se verifica el comportamiento HTTP del Controller.
 */
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    // ---------- Paso 15: prueba de la guia ----------

    @Test
    void postDebeRetornar201() throws Exception {
        when(service.crear(any(Producto.class)))
                .thenReturn(new Producto(1L, "Laptop", 3500.0, 10));

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Laptop","precio":3500,"stock":10}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    // ---------- Cobertura de la tabla del paso 14 ----------

    @Test
    void getListarDebeRetornar200() throws Exception {
        when(service.listar()).thenReturn(List.of(new Producto(1L, "Laptop", 3500.0, 10)));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }

    @Test
    void getPorIdDebeRetornar200SiExiste() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(new Producto(1L, "Laptop", 3500.0, 10)));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void getPorIdDebeRetornar404SiNoExiste() throws Exception {
        when(service.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void putDebeRetornar200SiExiste() throws Exception {
        when(service.actualizar(eq(1L), any(Producto.class)))
                .thenReturn(Optional.of(new Producto(1L, "Laptop Pro", 3900.0, 7)));

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Laptop Pro","precio":3900,"stock":7}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop Pro"))
                .andExpect(jsonPath("$.precio").value(3900.0));
    }

    @Test
    void putDebeRetornar404SiNoExiste() throws Exception {
        when(service.actualizar(eq(999L), any(Producto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Laptop Pro","precio":3900,"stock":7}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchPrecioDebeRetornar200() throws Exception {
        when(service.actualizarPrecio(1L, 4100.0))
                .thenReturn(Optional.of(new Producto(1L, "Laptop", 4100.0, 10)));

        mockMvc.perform(patch("/api/productos/1/precio").param("valor", "4100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(4100.0));
    }

    @Test
    void patchPrecioDebeRetornar404SiNoExiste() throws Exception {
        when(service.actualizarPrecio(eq(999L), anyDouble())).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/productos/999/precio").param("valor", "4100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDebeRetornar204SiExiste() throws Exception {
        when(service.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDebeRetornar404SiNoExiste() throws Exception {
        when(service.eliminar(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postConPrecioInvalidoDebeRetornar400() throws Exception {
        when(service.crear(any(Producto.class)))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor que cero"));

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Monitor","precio":0,"stock":5}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El precio debe ser mayor que cero"));
    }

    // ---------- Paso 16: reto integrador ----------

    @Test
    void getBuscarPorNombreDebeRetornar200ConCoincidencias() throws Exception {
        when(service.buscarPorNombre("lap")).thenReturn(List.of(
                new Producto(1L, "Laptop Lenovo", "Tecnologia", 3500.0, 10),
                new Producto(2L, "LAPTOP HP", "Tecnologia", 2800.0, 4)));

        mockMvc.perform(get("/api/productos/buscar").param("nombre", "lap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoria").value("Tecnologia"))
                .andExpect(jsonPath("$[1].nombre").value("LAPTOP HP"));
    }
}
