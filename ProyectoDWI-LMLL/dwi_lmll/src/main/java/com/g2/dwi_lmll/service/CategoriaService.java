package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.repository.CategoriaRepository;
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
