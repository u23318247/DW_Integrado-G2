package com.utp.tienda.service;

import com.utp.tienda.dto.ProductoRequest;
import com.utp.tienda.exception.RecursoNoEncontradoException;
import com.utp.tienda.model.Producto;
import com.utp.tienda.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio de productos.
 *
 * <p>Combina dos niveles de proteccion:</p>
 * <ul>
 *   <li><b>Nivel de URL</b>: reglas declaradas en el {@code SecurityFilterChain}.</li>
 *   <li><b>Nivel de metodo</b>: {@link PreAuthorize} en esta capa (Tarea 9).
 *       Sigue siendo efectivo aunque el servicio sea invocado desde otro punto
 *       de entrada que no pase por el filtro HTTP.</li>
 * </ul>
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Lectura: permitida para USER y ADMIN (regla aplicada en el filtro HTTP). */
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    /** Lectura: permitida para USER y ADMIN. */
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id);
    }

    /** Operacion critica: solo ADMIN, protegido tambien a nivel de metodo. */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto(
                request.nombre(),
                request.precio(),
                request.stock(),
                request.descripcion());
        return productoRepository.save(producto);
    }

    /** Operacion critica: solo ADMIN, protegido tambien a nivel de metodo. */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));

        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setDescripcion(request.descripcion());
        return productoRepository.save(producto);
    }

    /** Actualizacion parcial (PATCH): solo ADMIN. */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Producto actualizarParcial(Long id, Map<String, Object> cambios) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));

        cambios.forEach((campo, valor) -> {
            switch (campo) {
                case "nombre" -> producto.setNombre(String.valueOf(valor));
                case "descripcion" -> producto.setDescripcion(String.valueOf(valor));
                case "precio" -> producto.setPrecio(new BigDecimal(String.valueOf(valor)));
                case "stock" -> producto.setStock(Integer.valueOf(String.valueOf(valor)));
                case "activo" -> producto.setActivo(Boolean.valueOf(String.valueOf(valor)));
                default -> throw new IllegalArgumentException("Campo no actualizable: " + campo);
            }
        });
        return productoRepository.save(producto);
    }

    /**
     * Ejemplo canonico de la Tarea 9: seguridad a nivel de metodo con
     * {@code @PreAuthorize("hasRole('ADMIN')")} sobre una operacion critica.
     * Spring Security lanza {@code AccessDeniedException} (HTTP 403) si el rol no
     * cumple la expresion.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));
        productoRepository.delete(producto);
    }
}