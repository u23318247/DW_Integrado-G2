package com.utp.semana4;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debeListarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void debeBuscarProductoPorId() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop Lenovo"));
    }

    @Test
    void debeRetornar404CuandoProductoNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("No existe un producto con id: 999"));
    }

    @Test
    void debeCrearProducto() throws Exception {
        String json = """
                {
                    "nombre": "Tablet Xiaomi",
                    "categoria": "Tecnologia",
                    "precio": 1200.0,
                    "stock": 7
                }
                """;

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Tablet Xiaomi"));
    }

    @Test
    void debeRetornar400CuandoDatosCreacionSonInvalidos() throws Exception {
        String jsonInvalido = """
                {
                    "nombre": "",
                    "categoria": "Tecnologia",
                    "precio": -50.0,
                    "stock": -1
                }
                """;

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void debeActualizarProductoConPut() throws Exception {
        String json = """
                {
                    "nombre": "Laptop Lenovo ThinkPad",
                    "categoria": "Tecnologia",
                    "precio": 3899.90,
                    "stock": 12
                }
                """;

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$.precio").value(3899.90))
                .andExpect(jsonPath("$.stock").value(12));
    }

    @Test
    void debeActualizarStockConPatch() throws Exception {
        String json = """
                {
                    "stock": 20
                }
                """;

        mockMvc.perform(patch("/api/productos/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    void debeDisminuirStock() throws Exception {
        String json = """
                {
                    "cantidad": 2
                }
                """;

        mockMvc.perform(patch("/api/productos/2/disminuir-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void debeRetornar400AlDisminuirStockMayorAlDisponible() throws Exception {
        String json = """
                {
                    "cantidad": 9999
                }
                """;

        mockMvc.perform(patch("/api/productos/2/disminuir-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));
    }

    @Test
    void debeBuscarPorTexto() throws Exception {
        mockMvc.perform(get("/api/productos/buscar").param("texto", "lap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void debeEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos/3"))
                .andExpect(status().isNotFound());
    }
}
