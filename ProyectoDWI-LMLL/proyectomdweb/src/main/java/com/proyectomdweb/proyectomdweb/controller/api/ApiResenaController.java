package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.model.Resena;
import com.proyectomdweb.proyectomdweb.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ApiResenaController {

    private final ResenaRepository resenaRepository;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Map<String, Object>> listarPorProducto(@PathVariable Long productoId) {
        List<Resena> resenas = resenaRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
        double promedio = resenas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("productoId", productoId);
        res.put("total", resenas.size());
        res.put("promedio", Math.round(promedio * 10.0) / 10.0);
        res.put("resenas", resenas);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        Long productoId = Long.valueOf(String.valueOf(body.get("productoId")));
        String autor = String.valueOf(body.getOrDefault("autor", "Cliente LMLL")).trim();
        int calificacion = Integer.parseInt(String.valueOf(body.getOrDefault("calificacion", "5")));
        String comentario = String.valueOf(body.getOrDefault("comentario", "")).trim();

        if (calificacion < 1 || calificacion > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "La calificación debe estar entre 1 y 5."));
        }
        if (comentario.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El comentario es obligatorio."));
        }

        Resena guardada = resenaRepository.save(Resena.builder()
                .productoId(productoId)
                .autor(autor.isEmpty() ? "Cliente LMLL" : autor)
                .calificacion(calificacion)
                .comentario(comentario)
                .fechaCreacion(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(guardada);
    }
}
