package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Antonela Jaimes");
        usuario.setEmail("antonela@example.com");
        usuario.setTelefono("987654321");
        usuario.setDireccion("Lima, Perú");
        usuario.setPassword("password123");

        registroDTO = new UsuarioRegistroDTO();
        registroDTO.setNombre("Antonela Jaimes");
        registroDTO.setEmail("antonela@example.com");
        registroDTO.setTelefono("987654321");
        registroDTO.setDireccion("Lima, Perú");
        registroDTO.setPassword("password123");
    }

    @Test
    @DisplayName("Debe retornar true si el email ya existe")
    void existePorEmail_cuandoExiste_retornaTrue() {
        when(usuarioRepository.findByEmail("antonela@example.com")).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.existePorEmail("antonela@example.com");

        assertThat(resultado).isTrue();
        verify(usuarioRepository).findByEmail("antonela@example.com");
    }

    @Test
    @DisplayName("Debe retornar false si el email no existe")
    void existePorEmail_cuandoNoExiste_retornaFalse() {
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        boolean resultado = usuarioService.existePorEmail("noexiste@example.com");

        assertThat(resultado).isFalse();
        verify(usuarioRepository).findByEmail("noexiste@example.com");
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario y retornar su DTO")
    void registrarNuevoUsuario_exitoso() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = usuarioService.registrarNuevoUsuario(registroDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Antonela Jaimes");
        assertThat(resultado.getEmail()).isEqualTo("antonela@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe obtener DTO de usuario por email cuando existe")
    void obtenerUsuarioDtoPorEmail_cuandoExiste() {
        when(usuarioRepository.findByEmail("antonela@example.com")).thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.obtenerUsuarioDtoPorEmail("antonela@example.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("antonela@example.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email no existe al buscar DTO")
    void obtenerUsuarioDtoPorEmail_cuandoNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("desconocido@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerUsuarioDtoPorEmail("desconocido@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado con email: desconocido@example.com");
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void obtenerTodosLosUsuarios_retornaLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("antonela@example.com");
        verify(usuarioRepository).findAll();
    }
}
