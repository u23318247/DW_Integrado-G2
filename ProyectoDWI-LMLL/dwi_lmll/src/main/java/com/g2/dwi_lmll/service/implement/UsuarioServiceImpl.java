package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.model.Usuario;
import com.g2.dwi_lmll.repository.UsuarioRepository;
import com.g2.dwi_lmll.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    public boolean existePorEmail(String email){ return usuarioRepository.findByEmail(email).isPresent(); }

    public UsuarioDTO registrarNuevoUsuario(UsuarioRegistroDTO dto){
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        return convertir(usuarioRepository.save(usuario));
    }

    public UsuarioDTO obtenerUsuarioDtoPorEmail(String email){ return convertir(obtenerUsuarioEntidadPorEmail(email)); }
    public List<UsuarioDTO> obtenerTodosLosUsuarios(){ return usuarioRepository.findAll().stream().map(this::convertir).toList(); }
    public Usuario obtenerUsuarioEntidadPorEmail(String email){ return usuarioRepository.findByEmail(email).orElseThrow(); }

    private UsuarioDTO convertir(Usuario u){
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setDireccion(u.getDireccion());
        dto.setFechaRegistro(u.getFechaRegistro());
        return dto;
    }
}
