package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.model.Pedido;
import com.g2.dwi_lmll.repository.PedidoRepository;
import com.g2.dwi_lmll.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/pedidos", "/api/pedidos"})
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return pedidoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {
        return ResponseEntity.status(201).body(pedidoRepository.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        if (!pedidoRepository.existsById(id)) return ResponseEntity.notFound().build();
        pedido.setId(id);
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) return ResponseEntity.notFound().build();
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}