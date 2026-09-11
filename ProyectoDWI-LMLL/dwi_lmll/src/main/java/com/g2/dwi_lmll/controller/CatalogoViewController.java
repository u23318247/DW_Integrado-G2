package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.service.CategoriaService;
import com.g2.dwi_lmll.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class CatalogoViewController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String verCatalogo(Model model) {
        List<ProductoDTO> productos = productoService.listarTodos();
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("productos", productos);
        return "productos";
    }

    @GetMapping("/ajax")
    public String filtrarAjax(
            @RequestParam(defaultValue = "Unisex") String genero,
            @RequestParam(required = false) Long categoriaId,
            Model model) {
        List<ProductoDTO> productosFiltrados = productoService.filtrar(genero, categoriaId);
        model.addAttribute("productos", productosFiltrados);
        return "productos :: lista-productos";
    }
}
