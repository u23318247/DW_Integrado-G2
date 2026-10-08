package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.service.ProductoService;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;
import com.proyectomdweb.proyectomdweb.service.VentaPresentacionService; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductoService          productoService;
    private final UsuarioService           usuarioService;
    private final VentaPresentacionService ventaService; 
    private final com.proyectomdweb.proyectomdweb.repository.ProductoDetalleRepository productoDetalleRepository;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        
        model.addAttribute("totalProductos", productoService.listarTodos().size());
        model.addAttribute("totalClientes", usuarioService.obtenerTodosLosUsuarios().size());
        model.addAttribute("totalIngresos", ventaService.obtenerTotalIngresosReales());
        model.addAttribute("pedidosPendientes", ventaService.obtenerCantidadPedidosPendientes());
        
        // Prendas con stock bajo o agotándose (umbral: <= 25 unidades de prueba)
        model.addAttribute("productosStockBajo", productoDetalleRepository.findByStockLessThanEqualOrderByStockAsc(25));

        return "admin/dashboard"; 
    }
}