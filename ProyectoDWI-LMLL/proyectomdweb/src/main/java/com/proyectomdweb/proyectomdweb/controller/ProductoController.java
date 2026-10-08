package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller // Le dice a Spring que esta clase maneja peticiones web
@RequestMapping("/productos") // Todas las rutas aquí empezarán con /productos
@RequiredArgsConstructor // Lombok inyecta el servicio automáticamente
public class ProductoController {

    private final ProductoService  productoService;
    private final CategoriaService categoriaService;

    // Cuando el usuario entre a http://localhost:8080/productos
    @GetMapping
    public String listarTodosLosProductos(Model model) {
        
        List<ProductoDTO> listaProductos = productoService.listarTodos();

        // Se traen todas las categorías para llenar el sidebar de productos.html
        model.addAttribute("categorias", categoriaService.listarTodas());
        // Se listan los productos segun categoria
        model.addAttribute("productos", listaProductos);
        return "productos";
    }

    @GetMapping("/ajax") // La ruta completa será /productos/ajax
    public String filtrarAjax(
        @RequestParam(defaultValue = "Unisex") String genero,
        @RequestParam(required = false) Long categoriaId,
        Model model) {
    
        List<ProductoDTO> productosFiltrados = productoService.filtrar(genero, categoriaId);
            
        model.addAttribute("productos", productosFiltrados);
    
        // Thymeleaf buscará el fragmento "lista-productos" dentro de productos.html
        return "productos :: lista-productos";
        }

}