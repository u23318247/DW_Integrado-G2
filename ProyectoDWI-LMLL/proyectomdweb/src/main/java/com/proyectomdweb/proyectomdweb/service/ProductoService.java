package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    
    // Metodos de lectura que devuelven DTOs (Listos para la vista)
    List<ProductoDTO> listarTodos();
    Optional<ProductoDTO> buscarPorId(Long id);
    List<ProductoDTO> buscarPorCategoriaId(Long categoriaId);
    List<ProductoDTO> buscarPorNombre(String nombre);
    List<ProductoDTO> filtrar(String genero, Long categoriaId);
    
    // Métodos de escritura
    ProductoDTO guardar(ProductoDTO productoDto);
    void eliminar(Long id);
    
    // Método interno para otras capas (ej. Ventas) que necesiten la Entidad real
    Producto obtenerEntidadPorId(Long id);
}