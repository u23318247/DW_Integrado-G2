package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("Antonela Jaimes");
        usuarioDTO.setEmail("antonela@example.com");
        usuarioDTO.setTelefono("987654321");
        usuarioDTO.setDireccion("Lima, Perú");
    }

    @Test
    @DisplayName("GET /api/usuarios debe retornar lista de usuarios con status 200")
    void listarTodos_debeRetornar200YLista() throws Exception {
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(List.of(usuarioDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Antonela Jaimes"))
                .andExpect(jsonPath("$[0].email").value("antonela@example.com"));

        verify(usuarioService).obtenerTodosLosUsuarios();
    }

    @Test
    @DisplayName("GET /api/usuarios/email/{email} debe retornar usuario con status 200")
    void buscarPorEmail_debeRetornar200YUsuario() throws Exception {
        when(usuarioService.obtenerUsuarioDtoPorEmail("antonela@example.com")).thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/usuarios/email/antonela@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Antonela Jaimes"))
                .andExpect(jsonPath("$.email").value("antonela@example.com"));

        verify(usuarioService).obtenerUsuarioDtoPorEmail("antonela@example.com");
    }

    @Test
    @DisplayName("POST /api/usuarios con correo no registrado debe retornar status 201")
    void registrar_usuarioNuevo_debeRetornar201() throws Exception {
        when(usuarioService.existePorEmail("antonela@example.com")).thenReturn(false);
        when(usuarioService.registrarNuevoUsuario(any(UsuarioRegistroDTO.class))).thenReturn(usuarioDTO);

        String json = """
                {
                    "nombre": "Antonela Jaimes",
                    "email": "antonela@example.com",
                    "telefono": "987654321",
                    "direccion": "Lima, Perú",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Antonela Jaimes"))
                .andExpect(jsonPath("$.email").value("antonela@example.com"));
    }

    @Test
    @DisplayName("POST /api/usuarios con correo duplicado debe retornar status 409")
    void registrar_usuarioExistente_debeRetornar409() throws Exception {
        when(usuarioService.existePorEmail("antonela@example.com")).thenReturn(true);

        String json = """
                {
                    "nombre": "Antonela Jaimes",
                    "email": "antonela@example.com",
                    "telefono": "987654321",
                    "direccion": "Lima, Perú",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El correo ya está registrado"));
    }
}
