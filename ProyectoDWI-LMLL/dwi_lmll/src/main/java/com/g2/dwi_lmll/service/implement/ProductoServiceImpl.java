package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.mapper.ProductoMapper;
import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.repository.CategoriaRepository;
import com.g2.dwi_lmll.repository.ProductoRepository;
import com.g2.dwi_lmll.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
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
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrar(String genero, Long categoriaId) {
        Long idBusqueda = (categoriaId == null || categoriaId == 0) ? null : categoriaId;
        return productoRepository.filtrarPorGeneroYCategoria(genero, idBusqueda)
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO productoDto) {
        if (productoDto.nombre() == null || productoDto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDto.precioBase() == null || productoDto.precioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (productoDto.categoriaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        Categoria categoria = categoriaRepository.findById(productoDto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("La categoría seleccionada no existe."));

        Producto productoGuardado;
        if (productoDto.id() == null) {
            Producto nuevoProducto = productoMapper.toEntity(productoDto, categoria);
            productoGuardado = productoRepository.save(nuevoProducto);
        } else {
            Producto existente = productoRepository.findById(productoDto.id())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoDto.id()));

            existente.setNombre(productoDto.nombre());
            existente.setGenero(productoDto.genero());
            existente.setImagenUrl(productoDto.imagenUrl());
            existente.setPrecioBase(productoDto.precioBase());
            existente.setDisponibilidad(productoDto.disponibilidad());
            existente.setCategoria(categoria);

            productoGuardado = productoRepository.save(existente);
        }

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
    public Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
}
