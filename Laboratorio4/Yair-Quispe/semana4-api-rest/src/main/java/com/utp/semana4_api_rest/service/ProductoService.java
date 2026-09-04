package com.utp.semana4_api_rest.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

import com.utp.semana4_api_rest.dto.ActualizarStockRequest;
import com.utp.semana4_api_rest.dto.DisminuirStockRequest;
import com.utp.semana4_api_rest.dto.ProductoRequest;
import com.utp.semana4_api_rest.exception.ProductoNoEncontradoException;
import com.utp.semana4_api_rest.exception.StockInsuficienteException;
import com.utp.semana4_api_rest.model.producto;

@Service
public class ProductoService {

    private final Map<Long, producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    public ProductoService() {
        registrarInicial("Laptop Lenovo", "Tecnologia", 3500.00, 10);
        registrarInicial("Mouse Logitech", "Tecnologia", 80.00, 25);
        registrarInicial("Silla ergonomica", "Muebles", 750.00, 5);
    }

    public List<producto> listar(String categoria) {
        return productos.values()
                .stream()
                .filter(p -> categoria == null || p.getCategoria().equalsIgnoreCase(categoria))
                .sorted(Comparator.comparing(producto::getId))
                .toList();
    }

    public List<producto> buscarPorTexto(String texto) {
        return productos.values()
                .stream()
                .filter(p -> texto == null || p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .sorted(Comparator.comparing(producto::getId))
                .toList();
    }

    public producto buscarPorId(Long id) {
        producto producto = productos.get(id);
        if (producto == null) {
            throw new ProductoNoEncontradoException(id);
        }
        return producto;
    }

    public producto crear(ProductoRequest request) {
        Long id = secuencia.getAndIncrement();
        producto producto = new producto(
                id,
                request.getNombre(),
                request.getCategoria(),
                request.getPrecio(),
                request.getStock()
        );
        productos.put(id, producto);
        return producto;
    }

    public producto actualizar(Long id, ProductoRequest request) {
        producto producto = buscarPorId(id);
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        return producto;
    }

    public producto actualizarStock(Long id, ActualizarStockRequest request) {
        producto producto = buscarPorId(id);
        producto.setStock(request.getStock());
        return producto;
    }

    public producto disminuirStock(Long id, DisminuirStockRequest request) {
        producto producto = buscarPorId(id);
        if (request.getCantidad() > producto.getStock()) {
            throw new StockInsuficienteException(
                "Stock insuficiente. Stock actual: " + producto.getStock() + 
                ", cantidad solicitada a disminuir: " + request.getCantidad()
            );
        }
        producto.setStock(producto.getStock() - request.getCantidad());
        return producto;
    }

    public void eliminar(Long id) {
        producto eliminado = productos.remove(id);
        if (eliminado == null) {
            throw new ProductoNoEncontradoException(id);
        }
    }

    private void registrarInicial(String nombre, String categoria, double precio, int stock) {
        Long id = secuencia.getAndIncrement();
        productos.put(id, new producto(id, nombre, categoria, precio, stock));
    }
}