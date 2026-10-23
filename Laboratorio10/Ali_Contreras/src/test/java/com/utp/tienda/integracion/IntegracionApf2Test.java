package com.utp.tienda.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.MovimientoStockRepository;
import com.utp.tienda.repository.ProductoRepository;

/**
 * Matriz de pruebas de integracion de la semana 10 (seccion 13 de la guia), end-to-end:
 * login real -> JWT -> SecurityFilterChain -> Controller -> Service (@Transactional) -> Repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
class IntegracionApf2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoStockRepository movimientoRepository;

    private Long laptopId;

    @BeforeEach
    void datosDePrueba() {
        movimientoRepository.deleteAll();
        productoRepository.deleteAll();
        List<Producto> guardados = productoRepository.saveAll(List.of(
                new Producto("Laptop Lenovo ThinkPad", "Tecnologia", new BigDecimal("4200.00"), 8),
                new Producto("Mouse Logitech MX", "Tecnologia", new BigDecimal("320.00"), 20),
                new Producto("Silla ergonomica", "Muebles", new BigDecimal("850.00"), 6),
                new Producto("Escritorio ejecutivo", "Muebles", new BigDecimal("1200.00"), 4),
                new Producto("Monitor 27 pulgadas", "Tecnologia", new BigDecimal("1450.00"), 10)));
        laptopId = guardados.get(0).getId();
    }

    @Test
    void m01_loginUserValido_200ConToken() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"usuario\",\"password\":\"Usuario123*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void m02_productosSinToken_401() throws Exception {
        mockMvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
    }

    @Test
    void m03_productosConUser_200() throws Exception {
        mockMvc.perform(get("/api/productos").header("Authorization", bearerUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void m04_crearConUser_403() throws Exception {
        mockMvc.perform(post("/api/productos").header("Authorization", bearerUser())
                        .contentType(MediaType.APPLICATION_JSON).content(productoJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void m05_crearConAdmin_201() throws Exception {
        mockMvc.perform(post("/api/productos").header("Authorization", bearerAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(productoJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void m06_rangoJpqlConUser_200Filtrado() throws Exception {
        mockMvc.perform(get("/api/productos/rango").param("min", "100").param("max", "1000")
                        .header("Authorization", bearerUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Mouse Logitech MX"))
                .andExpect(jsonPath("$[1].nombre").value("Silla ergonomica"));
    }

    @Test
    void m07_salidaValidaConAdmin_200StockDisminuyeYMovimiento() throws Exception {
        mockMvc.perform(post("/api/inventario/{id}/salida", laptopId).param("cantidad", "2")
                        .header("Authorization", bearerAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(6));

        assertThat(stock(laptopId)).isEqualTo(6);
        assertThat(movimientoRepository.countByProductoId(laptopId)).isEqualTo(1);
    }

    @Test
    void m08_salidaStockInsuficiente_409YRollback() throws Exception {
        int stockAntes = stock(laptopId);
        long movimientosAntes = movimientoRepository.countByProductoId(laptopId);

        mockMvc.perform(post("/api/inventario/{id}/salida", laptopId).param("cantidad", "99999")
                        .header("Authorization", bearerAdmin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // Evidencia decisiva: el estado de la base de datos no cambio.
        assertThat(stock(laptopId)).isEqualTo(stockAntes);
        assertThat(movimientoRepository.countByProductoId(laptopId)).isEqualTo(movimientosAntes);
    }

    @Test
    void m09_resumenConUser_403() throws Exception {
        mockMvc.perform(get("/api/admin/resumen").header("Authorization", bearerUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    void m10_resumenConAdmin_200ConDatosReales() throws Exception {
        mockMvc.perform(post("/api/inventario/{id}/salida", laptopId).param("cantidad", "1")
                .header("Authorization", bearerAdmin()));

        mockMvc.perform(get("/api/admin/resumen").header("Authorization", bearerAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(5))
                .andExpect(jsonPath("$.productosConStockBajo").value(1))
                .andExpect(jsonPath("$.totalMovimientos").value(1))
                .andExpect(jsonPath("$.estado").value("BACKEND_OPERATIVO"));
    }

    @Test
    void m11_tokenAlterado_401() throws Exception {
        String token = bearerUser().substring("Bearer ".length());
        String[] partes = token.split("\\.");
        String firma = partes[2];
        char c = firma.charAt(firma.length() - 2);
        String alterado = partes[0] + "." + partes[1] + "."
                + firma.substring(0, firma.length() - 2) + (c == 'A' ? 'B' : 'A') + firma.charAt(firma.length() - 1);

        mockMvc.perform(get("/api/productos").header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void m12_productoInexistenteConAdmin_404() throws Exception {
        mockMvc.perform(post("/api/inventario/{id}/salida", 999999).param("cantidad", "1")
                        .header("Authorization", bearerAdmin()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/productos/{id}", 999999).header("Authorization", bearerAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void salidaConCantidadCero_400() throws Exception {
        mockMvc.perform(post("/api/inventario/{id}/salida", laptopId).param("cantidad", "0")
                        .header("Authorization", bearerAdmin()))
                .andExpect(status().isBadRequest());
        assertThat(movimientoRepository.countByProductoId(laptopId)).isZero();
    }

    @Test
    void salidaConUser_403() throws Exception {
        mockMvc.perform(post("/api/inventario/{id}/salida", laptopId).param("cantidad", "1")
                        .header("Authorization", bearerUser()))
                .andExpect(status().isForbidden());
        assertThat(stock(laptopId)).isEqualTo(8);
    }

    // ---------- Utilidades ----------

    private int stock(Long id) {
        return productoRepository.findById(id).orElseThrow().getStock();
    }

    private String productoJson() {
        return "{\"nombre\":\"Audifonos USB\",\"categoria\":\"Tecnologia\",\"precio\":190.00,\"stock\":7}";
    }

    private String bearerUser() throws Exception {
        return "Bearer " + login("usuario", "Usuario123*");
    }

    private String bearerAdmin() throws Exception {
        return "Bearer " + login("admin", "Admin123*");
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.get("accessToken").asText();
    }
}
