package com.proyectomdweb.proyectomdweb.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.proyectomdweb.proyectomdweb.exception.ResourceNotFoundException;
import com.proyectomdweb.proyectomdweb.model.Categoria;

@Service
public class CategoriaService {

    private final Map<Long, Categoria> categorias = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    public CategoriaService() {
        registrarInicial("Polos", "Polos estampados y oversize de algodón");
        registrarInicial("Chompas", "Chompas tejidas y abrigadoras de alpaca y lana");
        registrarInicial("Casacas", "Casacas bomber, impermeables y cortavientos");
        registrarInicial("Pantalones", "Pantalones cargo, jogger y jeans urbanos");
        registrarInicial("Accesorios", "Gorros, bufandas y accesorios andinos modernos");
    }

    public List<Categoria> listarTodas() {
        return categorias.values().stream()
                .sorted(Comparator.comparing(Categoria::getId))
                .toList();
    }

    public Categoria buscarPorId(Long id) {
        Categoria cat = categorias.get(id);
        if (cat == null) {
            throw new ResourceNotFoundException("No existe categoría con id: " + id);
        }
        return cat;
    }

    public Categoria crear(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        Long id = secuencia.getAndIncrement();
        categoria.setId(id);
        categorias.put(id, categoria);
        return categoria;
    }

    private void registrarInicial(String nombre, String descripcion) {
        Long id = secuencia.getAndIncrement();
        categorias.put(id, new Categoria(id, nombre, descripcion));
    }
}
