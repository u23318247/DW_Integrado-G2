package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.service.ProductoService;
import com.proyectomdweb.proyectomdweb.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/productos")
@RequiredArgsConstructor
public class AdminProductoController {

    private final ProductoService   productoService;
    private final CategoriaService  categoriaService;

    // --- (R) LEER: Muestra una tabla estilo Excel para el administrador --- //
    @GetMapping
    public String listarProductosAdmin(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "admin/lista-productos";
    }

     // --- (C) CREAR: Muestra el formulario vacío --- //
    @GetMapping("/nuevo")
    public String mostrarFormularioDeCrear(Model model) {
        model.addAttribute("producto", new ProductoDTO(null, "", "Unisex", "", null, true, null, ""));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/formulario-producto";
    }

    // --- (C) CREAR --> (U) ACTUALIZAR: Guarda los datos en la BD --- //
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoDTO productoDto,
                                  RedirectAttributes redirectAttributes) {
        try {
            // El controlador delega todo al servicio.
            productoService.guardar(productoDto);
            redirectAttributes.addFlashAttribute("exito", "Prenda guardada correctamente en el catálogo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/admin/productos/nuevo";
        }
        return "redirect:/admin/productos";
    }

    // --- (U) ACTUALIZAR: Muestra el formulario con los datos llenos --- //
    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model model,
                                            RedirectAttributes redirectAttributes) {
        var productoOpt = productoService.buscarPorId(id);
        
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La prenda con ID " + id + " no existe.");
            return "redirect:/admin/productos";
        }
        
        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/formulario-producto";
    }

    private final com.proyectomdweb.proyectomdweb.repository.ProductoDetalleRepository productoDetalleRepository;

    // --- (D) ELIMINAR: Borra la prenda de la BD --- //
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Producto eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    // --- (PROVEEDORES / REABASTECER STOCK) --- //
    @PostMapping("/detalles/reabastecer/{id}")
    public String reabastecerStock(
            @PathVariable Long id,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam(value = "proveedor", defaultValue = "Proveedor Local") String proveedor,
            RedirectAttributes redirectAttributes) {
        try {
            var detalleOpt = productoDetalleRepository.findById(id);
            if (detalleOpt.isPresent()) {
                var detalle = detalleOpt.get();
                int stockAnterior = detalle.getStock() != null ? detalle.getStock() : 0;
                detalle.setStock(stockAnterior + cantidad);
                productoDetalleRepository.save(detalle);
                redirectAttributes.addFlashAttribute("mensajeExito", 
                    "¡Stock ingresado! Se sumaron +" + cantidad + " unidades recibidas de [" + proveedor + "] a la prenda: " + detalle.getNombreCompleto());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al registrar ingreso de mercadería: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}