package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.service.ProductoService;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.repository.CategoriaRepository;
import com.g2.dwi_lmll.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoDTO> listarTodos() { return productoRepository.findAll().stream().map(this::toDto).toList(); }

    public Optional<ProductoDTO> buscarPorId(Long id) { return productoRepository.findById(id).map(this::toDto); }

    public List<ProductoDTO> buscarPorCategoriaId(Long categoriaId) {
        return productoRepository.findAll().stream().filter(p -> p.getCategoria()!=null && p.getCategoria().getId().equals(categoriaId)).map(this::toDto).toList();
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findAll().stream().filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase())).map(this::toDto).toList();
    }

    public List<ProductoDTO> filtrar(String genero, Long categoriaId) {
        return productoRepository.findAll().stream()
                .filter(p -> genero == null || p.getGenero().equalsIgnoreCase(genero))
                .filter(p -> categoriaId == null || (p.getCategoria()!=null && p.getCategoria().getId().equals(categoriaId)))
                .map(this::toDto).toList();
    }

    @Transactional
    public ProductoDTO guardar(ProductoDTO dto) {
        Producto p = new Producto();
        p.setId(dto.id());
        p.setNombre(dto.nombre());
        p.setGenero(dto.genero());
        p.setImagenUrl(dto.imagenUrl());
        p.setPrecioBase(dto.precioBase());
        p.setDisponibilidad(dto.disponibilidad());
        if (dto.categoriaId()!=null) p.setCategoria(categoriaRepository.findById(dto.categoriaId()).orElse(null));
        return toDto(productoRepository.save(p));
    }

    public void eliminar(Long id) { productoRepository.deleteById(id); }

    public Producto obtenerEntidadPorId(Long id) { return productoRepository.findById(id).orElseThrow(); }

    private ProductoDTO toDto(Producto p) {
        return new ProductoDTO(p.getId(), p.getNombre(), p.getGenero(), p.getImagenUrl(), p.getPrecioBase(), p.getDisponibilidad(),
                p.getCategoria()!=null?p.getCategoria().getId():null,
                p.getCategoria()!=null?p.getCategoria().getNombre():null);
    }

    
}
