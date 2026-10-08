package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.dtos.CategoriaDTO;
import com.proyectomdweb.proyectomdweb.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class ApiCategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> dtos = categoriaService.listarTodas().stream()
                .map(c -> new CategoriaDTO(c.getId(), c.getNombre()))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
