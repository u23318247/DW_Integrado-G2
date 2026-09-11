package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.repository.CategoriaRepository;
import com.g2.dwi_lmll.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        categoria.setId(null);
        Categoria creada = categoriaRepository.save(categoria);
        return ResponseEntity.created(URI.create("/api/categorias/" + creada.getId())).body(creada);
    }
}
