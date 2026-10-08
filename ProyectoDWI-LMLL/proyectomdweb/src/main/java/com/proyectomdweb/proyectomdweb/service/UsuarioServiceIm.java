package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.UsuarioRegistroDTO;
import com.proyectomdweb.proyectomdweb.dtos.UsuarioDTO;
import com.proyectomdweb.proyectomdweb.mapper.UsuarioMapper;
import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//UsuariServiceImplementacion es el nombre completo
public class UsuarioServiceIm implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper     usuarioMapper;
    private final PasswordEncoder   passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public UsuarioDTO registrarNuevoUsuario(UsuarioRegistroDTO registroDTO) {
        // 1. Convertir DTO a Entidad
        Usuario nuevoUsuario    = usuarioMapper.toEntity(registroDTO);
        
        // 2. Lógica de seguridad: Encriptar y asignar rol por defecto
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        if (nuevoUsuario.getRol() == null || nuevoUsuario.getRol().isBlank()) {
            nuevoUsuario.setRol("ROLE_CLIENTE");
        }
        
        // 3. Guardar en BD
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        
        // 4. Retornar DTO limpio
        return usuarioMapper.toDto(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDtoPorEmail(String email) {
        Usuario usuario = obtenerUsuarioEntidadPorEmail(email);
        return usuarioMapper.toDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
}
