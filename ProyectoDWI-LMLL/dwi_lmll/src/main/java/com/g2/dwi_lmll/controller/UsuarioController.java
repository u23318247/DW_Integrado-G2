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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    // GET /api/usuarios/email/correo@ejemplo.com
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioDtoPorEmail(email));
    }

    // POST /api/usuarios -> 201 si se crea, 409 si el correo ya existe
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRegistroDTO request) {
        if (usuarioService.existePorEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo ya está registrado"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarNuevoUsuario(request));
    }
}
