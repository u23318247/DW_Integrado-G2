package com.utp.semana4.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.utp.semana4.dto.ActualizarStockRequest;
import com.utp.semana4.dto.ProductoRequest;
import com.utp.semana4.exception.ProductoNoEncontradoException;
import com.utp.semana4.exception.StockInsuficienteException;
import com.utp.semana4.model.Producto;

@Service
public class ProductoService {

    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    public ProductoService() {
        registrarInicial("Laptop Lenovo", "Tecnologia", 3500.00, 10);
        registrarInicial("Mouse Logitech", "Tecnologia", 80.00, 25);
        registrarInicial("Silla ergonomica", "Muebles", 750.00, 5);
    }

    public List<Producto> listar(String categoria) {
        return productos.values()
                .stream()
                .filter(producto -> categoria == null
                        || producto.getCategoria().equalsIgnoreCase(categoria))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public Producto buscarPorId(Long id) {
        Producto producto = productos.get(id);
        if (producto == null) {
            throw new ProductoNoEncontradoException(id);
        }
        return producto;
    }

    public Producto crear(ProductoRequest request) {
        Long id = secuencia.getAndIncrement();
        Producto producto = new Producto(
                id,
                request.getNombre(),
                request.getCategoria(),
                request.getPrecio(),
                request.getStock()
        );
        productos.put(id, producto);
        return producto;
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        return producto;
    }

    public Producto actualizarStock(Long id, ActualizarStockRequest request) {
        Producto producto = buscarPorId(id);
        producto.setStock(request.getStock());
        return producto;
    }

    public void eliminar(Long id) {
        Producto eliminado = productos.remove(id);
        if (eliminado == null) {
            throw new ProductoNoEncontradoException(id);
        }
    }

    // Ejercicio complementario 1: Búsqueda por texto
    public List<Producto> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar(null);
        }
        return productos.values()
                .stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    // Ejercicio complementario 2: Disminuir stock con validación de no stock negativo
    public Producto disminuirStock(Long id, int cantidad) {
        Producto producto = buscarPorId(id);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor a cero");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException(
                    "Stock insuficiente. Stock actual: " + producto.getStock() + ", cantidad solicitada: " + cantidad);
        }
        producto.setStock(producto.getStock() - cantidad);
        return producto;
    }

    private void registrarInicial(String nombre, String categoria, double precio, int stock) {
        Long id = secuencia.getAndIncrement();
        productos.put(id, new Producto(id, nombre, categoria, precio, stock));
    }
}
