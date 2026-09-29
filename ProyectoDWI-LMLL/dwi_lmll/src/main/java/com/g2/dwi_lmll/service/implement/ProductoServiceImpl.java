package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.mapper.ProductoMapper;
import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.model.MovimientoStock;
import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.model.enums.TipoMovimiento;
import com.g2.dwi_lmll.exception.ProductoNoEncontradoException;
import com.g2.dwi_lmll.repository.MovimientoStockRepository;
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
    private final MovimientoStockRepository movimientoStockRepository;

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

    // ===================== LABORATORIO 7: TRANSACCIONES =====================

    /**
     * Descuenta stock y registra el movimiento SALIDA en UNA sola transacción.
     * Si algo falla, se revierte todo (atomicidad).
     *
     * Dirty checking: el producto se carga dentro de la transacción, por lo que
     * queda "gestionado" por el EntityManager. Al modificar su stock NO hace falta
     * llamar a productoRepository.save(): al hacer commit Hibernate compara el
     * estado actual con el original y emite el UPDATE automáticamente.
     */
    @Override
    @Transactional
    public void registrarSalida(Long id, int cantidad) {
        validarCantidad(cantidad);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente: disponible "
                    + producto.getStock() + ", solicitado " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad); // dirty checking: sin save()

        movimientoStockRepository.save(MovimientoStock.builder()
                .producto(producto)
                .tipo(TipoMovimiento.SALIDA)
                .cantidad(cantidad)
                .build());
    }

    /**
     * Actividad de consolidación: aumenta stock y registra el movimiento ENTRADA.
     */
    @Override
    @Transactional
    public void registrarEntrada(Long id, int cantidad) {
        validarCantidad(cantidad);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));

        producto.setStock(producto.getStock() + cantidad); // dirty checking: sin save()

        movimientoStockRepository.save(MovimientoStock.builder()
                .producto(producto)
                .tipo(TipoMovimiento.ENTRADA)
                .cantidad(cantidad)
                .build());
    }

    /**
     * Demostración de rollback: descuenta stock y guarda el movimiento, y luego
     * lanza una RuntimeException. Spring hace rollback automático, por lo que ni
     * el UPDATE del producto ni el INSERT del movimiento quedan en la base de datos.
     */
    @Override
    @Transactional
    public void simularSalidaConError(Long id, int cantidad) {
        registrarSalida(id, cantidad); // se une a la misma transacción (REQUIRED)
        throw new RuntimeException("Fallo simulado despues de descontar stock y guardar el movimiento");
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }
}
