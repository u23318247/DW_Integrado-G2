package com.proyectomdweb.proyectomdweb.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import com.proyectomdweb.proyectomdweb.dto.ActualizarStockRequest;
import com.proyectomdweb.proyectomdweb.dto.ProductoRequest;
import com.proyectomdweb.proyectomdweb.exception.ResourceNotFoundException;
import com.proyectomdweb.proyectomdweb.exception.StockInsuficienteException;
import com.proyectomdweb.proyectomdweb.model.Producto;

@Service
public class ProductoService {

    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    public ProductoService() {
        registrarInicial("Polo Oversize Llama Classic", "Polos", "Unisex", "/img/llama-a-la-moda.PNG", 49.90, 35);
        registrarInicial("Casaca Bomber Urbana", "Casacas", "Hombre", "/img/Imagen-mov-1.jpg", 139.90, 15);
        registrarInicial("Chompa Alpaca Andina", "Chompas", "Mujer", "/img/llama a la moda.PNG", 120.00, 20);
        registrarInicial("Pantalon Cargo Beige", "Pantalones", "Unisex", "/img/Imagen-mov-2.PNG", 89.90, 25);
        registrarInicial("Gorro Llama Beanie", "Accesorios", "Unisex", "/img/logo.png", 29.90, 40);
    }

    public List<Producto> listar(String categoria, String genero) {
        return productos.values().stream()
                .filter(p -> categoria == null || p.getCategoria().equalsIgnoreCase(categoria))
                .filter(p -> genero == null || p.getGenero().equalsIgnoreCase(genero))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public Producto buscarPorId(Long id) {
        Producto producto = productos.get(id);
        if (producto == null) {
            throw new ResourceNotFoundException("No existe prenda con id: " + id);
        }
        return producto;
    }

    public List<Producto> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar(null, null);
        }
        return productos.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public Producto crear(ProductoRequest request) {
        Long id = secuencia.getAndIncrement();
        Producto producto = new Producto(
                id,
                request.getNombre(),
                request.getCategoria(),
                request.getGenero(),
                request.getImagenUrl() != null ? request.getImagenUrl() : "/img/logo.png",
                request.getPrecio(),
                request.getStock(),
                request.getStock() > 0
        );
        productos.put(id, producto);
        return producto;
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setGenero(request.getGenero());
        if (request.getImagenUrl() != null) {
            producto.setImagenUrl(request.getImagenUrl());
        }
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDisponible(request.getStock() > 0);
        return producto;
    }

    public Producto actualizarStock(Long id, ActualizarStockRequest request) {
        Producto producto = buscarPorId(id);
        producto.setStock(request.getStock());
        producto.setDisponible(request.getStock() > 0);
        return producto;
    }

    public Producto disminuirStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor a cero");
        }
        Producto producto = buscarPorId(id);
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + producto.getNombre() + "'. Stock actual: " + producto.getStock() + ", solicitado: " + cantidad);
        }
        int nuevoStock = producto.getStock() - cantidad;
        producto.setStock(nuevoStock);
        producto.setDisponible(nuevoStock > 0);
        return producto;
    }

    public void eliminar(Long id) {
        Producto eliminado = productos.remove(id);
        if (eliminado == null) {
            throw new ResourceNotFoundException("No existe prenda con id: " + id);
        }
    }

    private void registrarInicial(String nombre, String categoria, String genero, String imagenUrl, double precio, int stock) {
        Long id = secuencia.getAndIncrement();
        productos.put(id, new Producto(id, nombre, categoria, genero, imagenUrl, precio, stock, stock > 0));
    }
}
