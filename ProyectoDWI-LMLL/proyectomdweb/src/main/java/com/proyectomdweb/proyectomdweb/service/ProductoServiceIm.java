package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.mapper.ProductoMapper;
import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.repository.CategoriaRepository;
import com.proyectomdweb.proyectomdweb.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceIm implements ProductoService {
    
    private final ProductoRepository  productoRepository;
    private final ProductoMapper      productoMapper; 
    private final CategoriaRepository categoriaRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAllConCategoria()
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findByIdConCategoria(id)
                .map(productoMapper::toDto);
    }
    
    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO productoDto) {
        // 1. Validaciones usan los métodos del record 
        if (productoDto.nombre() == null || productoDto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDto.precioBase() == null || productoDto.precioBase().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }

        // 2. Se busca la categoría en la BD usando el ID que viene en el DTO
        Categoria categoria = null;
        if (productoDto.categoriaId() != null) {
            categoria = categoriaRepository.findById(productoDto.categoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("La categoría seleccionada no existe."));
        } else {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }
        Producto productoGuardado;

        // 3. Lógica de Creacion y Actualización
        if (productoDto.id() == null) {
            // Si es NUEVO: Usa el mapper para convertir el DTO a Entidad
            Producto nuevoProducto = productoMapper.toEntity(productoDto, categoria);
            productoGuardado = productoRepository.save(nuevoProducto);
        } else {
            // Si es ACTUALIZACIÓN: Busca la entidad real en la BD
            Producto existente = productoRepository.findById(productoDto.id())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoDto.id()));

            // Actualiza los datos de la entidad con la información fresca del DTO
            existente.setNombre(productoDto.nombre());
            existente.setGenero(productoDto.genero());
            existente.setImagenUrl(productoDto.imagenUrl());
            existente.setPrecioBase(productoDto.precioBase());
            existente.setDisponibilidad(productoDto.disponibilidad());
            existente.setCategoria(categoria); 

            productoGuardado = productoRepository.save(existente);
        }
        
        // 4. Mapea la entidad guardada de vuelta a DTO para devolverla al controlador
        return productoMapper.toDto(productoGuardado);
    }
    
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: el producto no existe.");
        }
        productoRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId)
                .stream().map(productoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(productoMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrar(String genero, Long categoriaId) {
        Long idBusqueda = (categoriaId == null || categoriaId == 0) ? null : categoriaId;
        return productoRepository.filtrarPorGeneroYCategoria(genero, idBusqueda)
                .stream().map(productoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}