package com.utp.tienda.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.utp.tienda.config.JwtConfig;
import com.utp.tienda.config.SecurityConfig;
import com.utp.tienda.dto.ResumenBackendResponse;
import com.utp.tienda.service.AdminResumenService;
import com.utp.tienda.service.ReporteService;

/**
 * Prueba de la guia (paso 10): contrato de seguridad de /api/admin/resumen con una
 * autenticacion JWT simulada por Spring Security Test (sin pasar por /login).
 *
 * Spring Boot 3.5.x: @WebMvcTest se importa de org.springframework.boot.test.autoconfigure.web.servlet;
 * se importan SecurityConfig y JwtConfig y se mockea UserDetailsService (lo usa el AuthenticationManager).
 */
@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class AdminControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AdminResumenService service;

    @MockitoBean
    ReporteService reporteService;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    void adminPuedeConsultarResumen() throws Exception {
        when(service.obtenerResumen())
                .thenReturn(new ResumenBackendResponse(5, 1, 3, "BACKEND_OPERATIVO"));

        mockMvc.perform(get("/api/admin/resumen")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(5))
                .andExpect(jsonPath("$.productosConStockBajo").value(1))
                .andExpect(jsonPath("$.totalMovimientos").value(3))
                .andExpect(jsonPath("$.estado").value("BACKEND_OPERATIVO"));
    }

    @Test
    void userNoPuedeConsultarResumen() throws Exception {
        mockMvc.perform(get("/api/admin/resumen")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenNoPuedeConsultarResumen() throws Exception {
        mockMvc.perform(get("/api/admin/resumen"))
                .andExpect(status().isUnauthorized());
    }
}
