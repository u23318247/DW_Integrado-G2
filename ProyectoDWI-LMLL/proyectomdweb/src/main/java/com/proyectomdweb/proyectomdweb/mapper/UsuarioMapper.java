package com.proyectomdweb.proyectomdweb.mapper;

import com.proyectomdweb.proyectomdweb.dtos.UsuarioRegistroDTO;
import com.proyectomdweb.proyectomdweb.dtos.UsuarioDTO;
import com.proyectomdweb.proyectomdweb.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    //*  Convierte de Entidad a DTO (Salida) *//
    public UsuarioDTO toDto(Usuario usuario) {
        if (usuario == null) return null;

            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());
            dto.setTelefono(usuario.getTelefono());
            dto.setDireccion(usuario.getDireccion());
            dto.setFechaRegistro(usuario.getFechaRegistro());
        return dto;
    }
    //*  Convierte de DTO (Entrada) a Entidad *//
    public Usuario toEntity(UsuarioRegistroDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        //* No se mapea la contraseña porque el controlador debe encriptarla antes de guardarla. *//
        return usuario;
    }
}