package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.model.Pedido;
import com.proyectomdweb.proyectomdweb.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ApiPedidoController {

    private final PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<List<Pedido>> listar(
            @RequestParam(required = false) Long usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(pedidoRepository.findByUsuarioIdOrderByIdDesc(usuarioId));
        }
        return ResponseEntity.ok(pedidoRepository.findAllByOrderByIdDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
