package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.UsuarioDTO;
import com.g2.dwi_lmll.dto.UsuarioRegistroDTO;
import com.g2.dwi_lmll.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(){
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable String email){
        return ResponseEntity.ok(usuarioService.obtenerUsuarioDtoPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioRegistroDTO request){
        return ResponseEntity.status(201).body(usuarioService.registrarNuevoUsuario(request));
    }
}
