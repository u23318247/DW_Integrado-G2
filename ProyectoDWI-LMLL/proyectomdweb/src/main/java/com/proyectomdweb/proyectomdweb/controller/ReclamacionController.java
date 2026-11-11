package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.model.Reclamacion;
import com.proyectomdweb.proyectomdweb.service.ReclamacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para Libro de Reclamaciones Virtual
 * Vistas y API REST
 * Alumna: Antonela Jaimes
 */
@Controller
@RequiredArgsConstructor
public class ReclamacionController {

    private final ReclamacionService reclamacionService;

    @GetMapping("/reclamaciones")
    public String mostrarFormulario(Model model) {
        model.addAttribute("titulo", "Libro de Reclamaciones Virtual - La Moda te LLama");
        return "reclamaciones";
    }

    @PostMapping("/api/reclamaciones")
    @ResponseBody
    public ResponseEntity<?> registrarReclamacion(@RequestBody Reclamacion reclamacion) {
        if (reclamacion.getNombresApellidos() == null || reclamacion.getEmail() == null || reclamacion.getDetalleReclamo() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "mensaje", "Todos los campos obligatorios deben ser completados."
            ));
        }

        Reclamacion guardada = reclamacionService.registrarReclamacion(reclamacion);
        return ResponseEntity.ok(Map.of(
            "exito", true,
            "codigoReclamo", guardada.getCodigoReclamo(),
            "mensaje", "Su reclamación ha sido registrada exitosamente. Se ha enviado una copia a su correo.",
            "plazoDiasHabiles", 15
        ));
    }

    @GetMapping("/api/reclamaciones/{codigo}")
    @ResponseBody
    public ResponseEntity<?> consultarReclamacion(@PathVariable String codigo) {
        return reclamacionService.consultarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
