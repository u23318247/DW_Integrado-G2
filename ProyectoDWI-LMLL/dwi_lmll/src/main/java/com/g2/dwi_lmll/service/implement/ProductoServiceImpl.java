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
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAllConCategoria().stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findByIdConCategoria(id)
                .map(productoMapper::toDto);
    }

    @Override
    public List<ProductoDTO> buscarPorCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductoDTO> filtrar(String genero, Long categoriaId) {
        return productoRepository.filtrarPorGeneroYCategoria(genero, categoriaId).stream()
                .map(productoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO dto) {
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (dto.precioBase() == null || dto.precioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (dto.categoriaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + dto.categoriaId()));

        Producto producto = productoMapper.toEntity(dto, categoria);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDto(guardado);
    }

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: el producto no existe.");
        }
        productoRepository.deleteById(id);
    }

    @Override
    public Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
}
