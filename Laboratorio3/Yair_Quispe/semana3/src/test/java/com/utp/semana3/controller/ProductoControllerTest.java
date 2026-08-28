package com.utp.semana3.controller;
import com.utp.semana3.model.Producto;
import com.utp.semana3.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {
@Autowired
private MockMvc mockMvc;
@MockitoBean
private ProductoService productoService;
@Test
void listar_debeRetornarProductosEnJson() throws Exception {
when(productoService.listar()).thenReturn(List.of(
new Producto(1L, "Laptop", 3500.00, 10),
new Producto(2L, "Mouse", 80.00, 20)
));
mockMvc.perform(get("/productos"))
.andExpect(status().isOk())
.andExpect(jsonPath("$[0].nombre").value("Laptop"))
.andExpect(jsonPath("$[1].nombre").value("Mouse"));
}
}