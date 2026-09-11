package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.repository.UsuarioRepository;
import com.g2.dwi_lmll.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        nuevoUsuario.setPassword(registroDTO.getPassword()); // Sin encriptar por ahora (primera entrega)
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setDireccion(registroDTO.getDireccion());
        return convertirADto(usuarioRepository.save(nuevoUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDtoPorEmail(String email) {
        return convertirADto(obtenerUsuarioEntidadPorEmail(email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream().map(this::convertirADto).toList();
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
