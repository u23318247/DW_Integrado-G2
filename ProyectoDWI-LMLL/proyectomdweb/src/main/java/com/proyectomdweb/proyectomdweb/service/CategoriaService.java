package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Error: La categoría con ID " + id + " no existe."));
    }
}
