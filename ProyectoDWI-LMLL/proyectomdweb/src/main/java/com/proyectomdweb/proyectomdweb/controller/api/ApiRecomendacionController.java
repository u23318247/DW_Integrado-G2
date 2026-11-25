package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recomendaciones")
@RequiredArgsConstructor
public class ApiRecomendacionController {

    private final ProductoService productoService;

    @GetMapping("/producto/{id}")
    public ResponseEntity<Map<String, Object>> recomendarOutfit(@PathVariable Long id) {
        Optional<ProductoDTO> baseOpt = productoService.buscarPorId(id);
        if (baseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductoDTO base = baseOpt.get();
        List<ProductoDTO> todos = productoService.listarTodos();

        List<ProductoDTO> sugerencias = new ArrayList<>();
        Set<Long> categoriasUsadas = new HashSet<>();
        if (base.categoriaId() != null) {
            categoriasUsadas.add(base.categoriaId());
        }

        for (ProductoDTO p : todos) {
            if (p.id().equals(base.id())) continue;
            if (!Boolean.TRUE.equals(p.disponibilidad())) continue;
            if (p.categoriaId() != null && categoriasUsadas.contains(p.categoriaId())) continue;
            boolean generoCompatible = "Unisex".equalsIgnoreCase(base.genero())
                    || "Unisex".equalsIgnoreCase(p.genero())
                    || base.genero().equalsIgnoreCase(p.genero());
            if (!generoCompatible) continue;

            sugerencias.add(p);
            if (p.categoriaId() != null) {
                categoriasUsadas.add(p.categoriaId());
            }
            if (sugerencias.size() >= 4) break;
        }

        return ResponseEntity.ok(Map.of(
                "productoBase", base.nombre(),
                "outfitSugerido", sugerencias
        ));
    }
}
