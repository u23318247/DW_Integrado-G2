package com.g2.dwi_lmll.controller;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.repository.PedidoRepository;
import com.g2.dwi_lmll.repository.VentaRepository;
import com.g2.dwi_lmll.service.CategoriaService;
import com.g2.dwi_lmll.service.ProductoService;
import com.g2.dwi_lmll.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CatalogoViewController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final VentaRepository ventaRepository;

    @Autowired
    public CatalogoViewController(
            ProductoService productoService,
            CategoriaService categoriaService,
            UsuarioService usuarioService,
            PedidoRepository pedidoRepository,
            VentaRepository ventaRepository) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.pedidoRepository = pedidoRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping({"/", "/productos", "/index", "/catalogo"})
    public String verCatalogo(Model model) {
        List<ProductoDTO> productos = productoService.listarTodos();
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("productos", productos);
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("ventas", ventaRepository.findAll());
        return "productos";
    }

    @GetMapping("/productos/ajax")
    public String filtrarAjax(
            @RequestParam(defaultValue = "Unisex") String genero,
            @RequestParam(required = false) Long categoriaId,
            Model model) {
        List<ProductoDTO> productosFiltrados = productoService.filtrar(genero, categoriaId);
        model.addAttribute("productos", productosFiltrados);
        return "productos :: lista-productos";
    }
}
