package com.proyectomdweb.proyectomdweb;

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
    void debeListarPrendasEnJson() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].nombre").exists());
    }

    @Test
    void debeFiltrarPorCategoriaYGenero() throws Exception {
        mockMvc.perform(get("/api/productos")
                        .param("categoria", "Polos")
                        .param("genero", "Unisex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Polos"));
    }

    @Test
    void debeBuscarPrendaPorId() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void debeRetornar404CuandoPrendaNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void debeCrearPrendaConPost() throws Exception {
        String json = """
                {
                    "nombre": "Polera Estampada Llama",
                    "categoria": "Chompas",
                    "genero": "Unisex",
                    "precio": 89.90,
                    "stock": 15
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Polera Estampada Llama"));
    }

    @Test
    void debeRetornar400SiDatosCreacionSonInvalidos() throws Exception {
        String jsonInvalido = """
                {
                    "nombre": "",
                    "categoria": "",
                    "genero": "",
                    "precio": -10.0,
                    "stock": -5
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));
    }

    @Test
    void debeActualizarPrendaConPut() throws Exception {
        String json = """
                {
                    "nombre": "Polo Oversize Llama Classic V2",
                    "categoria": "Polos",
                    "genero": "Unisex",
                    "precio": 55.00,
                    "stock": 40
                }
                """;

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Polo Oversize Llama Classic V2"))
                .andExpect(jsonPath("$.precio").value(55.00));
    }

    @Test
    void debeActualizarStockConPatch() throws Exception {
        String json = """
                {
                    "stock": 50
                }
                """;

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void debeDisminuirStockConPatch() throws Exception {
        String json = """
                {
                    "cantidad": 5
                }
                """;

        mockMvc.perform(patch("/api/productos/1/disminuir-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void debeEliminarPrendaConDelete() throws Exception {
        mockMvc.perform(delete("/api/productos/5"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos/5"))
                .andExpect(status().isNotFound());
    }
}
