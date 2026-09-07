package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.model.Usuario;

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