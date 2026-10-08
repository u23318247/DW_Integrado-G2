package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.UsuarioRegistroDTO;
import com.proyectomdweb.proyectomdweb.dtos.UsuarioDTO;
import com.proyectomdweb.proyectomdweb.model.Usuario;

import java.util.List;

public interface UsuarioService {
    
    // Metodos para el Registro
    boolean existePorEmail(String email);
    UsuarioDTO registrarNuevoUsuario(UsuarioRegistroDTO registroDTO);

    // Metodos para Controladores de Vistas (Devuelven DTOs)
    UsuarioDTO obtenerUsuarioDtoPorEmail(String email);
    List<UsuarioDTO> obtenerTodosLosUsuarios(); // Para futuro

    // Metodo para Controladores Internos (Devuelve la Entidad real para enlazar bases de datos)
    Usuario obtenerUsuarioEntidadPorEmail(String email); 
}
