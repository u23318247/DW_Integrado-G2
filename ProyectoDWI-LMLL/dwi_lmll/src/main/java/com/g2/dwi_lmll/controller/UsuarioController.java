package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET http://localhost:8080/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // GET http://localhost:8080/usuarios/correo@ejemplo.com
    @GetMapping("/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = usuarioService.obtenerUsuarioDtoPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    // POST http://localhost:8080/usuarios/registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        if (usuarioService.existePorEmail(registroDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo ya está registrado"));
        }
        UsuarioDTO nuevoUsuario = usuarioService.registrarNuevoUsuario(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
}