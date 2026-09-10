package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public UsuarioDTO registrarNuevoUsuario(UsuarioRegistroDTO registroDTO) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setDireccion(registroDTO.getDireccion());
        nuevoUsuario.setPassword(registroDTO.getPassword()); // Sin encriptar por ahora (primera entrega)

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return convertirADto(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDtoPorEmail(String email) {
        Usuario usuario = obtenerUsuarioEntidadPorEmail(email);
        return convertirADto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    private UsuarioDTO convertirADto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        return dto;
    }
}