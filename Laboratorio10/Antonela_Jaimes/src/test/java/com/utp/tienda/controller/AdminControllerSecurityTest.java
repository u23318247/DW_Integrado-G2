package com.utp.tienda.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.tienda.dto.ResumenBackendResponse;
import com.utp.tienda.service.AdminResumenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminResumenService service;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADMIN puede consultar el resumen administrativo (HTTP 200)")
    void adminPuedeConsultarResumen() throws Exception {
        when(service.obtenerResumen())
                .thenReturn(new ResumenBackendResponse(5, 1, 3, "BACKEND_OPERATIVO"));

        mockMvc.perform(get("/api/admin/resumen"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "usuario", roles = {"USER"})
    @DisplayName("USER no puede consultar el resumen administrativo (HTTP 403 Forbidden)")
    void userNoPuedeConsultarResumen() throws Exception {
        mockMvc.perform(get("/api/admin/resumen"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Petición sin autenticación es rechazada con HTTP 403 / 401")
    void usuarioSinAutenticarEsRechazado() throws Exception {
        mockMvc.perform(get("/api/admin/resumen"))
                .andExpect(status().isForbidden());
    }
}
