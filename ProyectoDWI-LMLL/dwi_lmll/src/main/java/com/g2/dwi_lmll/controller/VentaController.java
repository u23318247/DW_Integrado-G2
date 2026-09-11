package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.model.Venta;
import com.g2.dwi_lmll.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/ventas", "/api/ventas"})
@RequiredArgsConstructor
public class VentaController {
    private final VentaRepository ventaRepository;

    @GetMapping
    public ResponseEntity<List<Venta>> listar(){ return ResponseEntity.ok(ventaRepository.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscar(@PathVariable Long id){ return ventaRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody Venta venta){ return ResponseEntity.status(201).body(ventaRepository.save(venta)); }
}
