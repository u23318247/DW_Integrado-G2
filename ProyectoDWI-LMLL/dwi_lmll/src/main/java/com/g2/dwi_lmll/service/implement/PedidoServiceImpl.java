package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;
import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.repository.PedidoRepository;
import com.g2.dwi_lmll.repository.ProductoRepository;
import com.g2.dwi_lmll.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final ProductoRepository productoRepository;

    @Override
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<ProductoDTO> buscarPorCategoriaId(Long id) {
        return productoRepository.findByCategoriaId(id).stream().map(this::toDto).toList();
    }

    @Override
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream().map(this::toDto).toList();
    }

    @Override
    public List<ProductoDTO> filtrar(String genero, Long categoriaId) {
        return productoRepository.filtrarPorGeneroYCategoria(genero, categoriaId).stream().map(this::toDto).toList();
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id).orElseThrow();
    }

    private ProductoDTO toDto(Producto p) {
        return new ProductoDTO(p.getId(), p.getNombre(), p.getGenero(), p.getImagenUrl(), p.getPrecioBase(), p.getDisponibilidad(), p.getCategoria() != null ? p.getCategoria().getId() : null, p.getCategoria() != null ? p.getCategoria().getNombre() : null);
    }
}