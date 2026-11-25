package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.model.Favorito;
import com.proyectomdweb.proyectomdweb.repository.FavoritoRepository;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class ApiFavoritoController {

    private final FavoritoRepository favoritoRepository;
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar(
            @RequestParam(defaultValue = "invitado") String usuario) {
        List<Favorito> favs = favoritoRepository.findByUsuarioClaveOrderByFechaAgregadoDesc(usuario.trim());
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Favorito f : favs) {
            Optional<ProductoDTO> prod = productoService.buscarPorId(f.getProductoId());
            if (prod.isPresent()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("favoritoId", f.getId());
                item.put("fechaAgregado", f.getFechaAgregado());
                item.put("producto", prod.get());
                respuesta.add(item);
            }
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@RequestBody Map<String, Object> body) {
        String usuario = String.valueOf(body.getOrDefault("usuario", "invitado")).trim();
        Long productoId = Long.valueOf(String.valueOf(body.get("productoId")));

        var existente = favoritoRepository.findByUsuarioClaveAndProductoId(usuario, productoId);
        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get());
            return ResponseEntity.ok(Map.of("enFavoritos", false, "productoId", productoId, "usuario", usuario));
        }

        Favorito nuevo = favoritoRepository.save(Favorito.builder()
                .usuarioClave(usuario)
                .productoId(productoId)
                .fechaAgregado(LocalDateTime.now())
                .build());
        return ResponseEntity.ok(Map.of("enFavoritos", true, "favoritoId", nuevo.getId(), "productoId", productoId, "usuario", usuario));
    }
}
