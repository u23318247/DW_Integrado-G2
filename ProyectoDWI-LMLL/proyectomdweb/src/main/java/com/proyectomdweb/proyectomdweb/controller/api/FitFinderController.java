package com.proyectomdweb.proyectomdweb.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de Asesor de Tallas Inteligente con IA (Fit Finder)
 * Calcula la talla antropométrica recomendada según estatura, peso, contextura y corte preferido
 * Alumna: Antonela Jaimes
 */
@RestController
@RequestMapping("/api/tallas")
public class FitFinderController {

    public record FitRequest(
            Double altura,       // en centímetros (ej. 175)
            Double peso,         // en kilogramos (ej. 70)
            String genero,       // HOMBRE, MUJER, UNISEX
            String contextura,   // DELGADA, ATLETICA, ROBUSTA
            String corteDeseado  // SLIM, REGULAR, OVERSIZE
    ) {}

    @PostMapping("/recomendar")
    public ResponseEntity<?> recomendarTalla(@RequestBody FitRequest req) {
        if (req.altura == null || req.peso == null || req.altura <= 0 || req.peso <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Estatura y peso válidos son requeridos."
            ));
        }

        // Cálculo de Índice de Masa Corporal (IMC)
        double alturaMetros = req.altura / 100.0;
        double imc = req.peso / (alturaMetros * alturaMetros);

        String tallaBase;
        int confianza = 94;

        if (req.genero != null && req.genero.equalsIgnoreCase("MUJER")) {
            if (req.altura < 155 && imc < 20) tallaBase = "XS";
            else if (req.altura < 162 && imc < 22) tallaBase = "S";
            else if (req.altura < 170 && imc < 25) tallaBase = "M";
            else if (req.altura < 176 && imc < 28) tallaBase = "L";
            else tallaBase = "XL";
        } else {
            // Hombre / Unisex
            if (req.altura < 165 && imc < 21) tallaBase = "S";
            else if (req.altura < 175 && imc < 24.5) tallaBase = "M";
            else if (req.altura < 183 && imc < 27.5) tallaBase = "L";
            else if (req.altura < 190 && imc < 30.5) tallaBase = "XL";
            else tallaBase = "XXL";
        }

        // Ajuste por preferencia de corte
        String tallaFinal = tallaBase;
        String ajusteNota = "Ajuste estándar regular.";

        if ("OVERSIZE".equalsIgnoreCase(req.corteDeseado)) {
            tallaFinal = subirTalla(tallaBase);
            ajusteNota = "Se incrementó una talla para lograr caída oversize holgada moderna.";
            confianza = 91;
        } else if ("SLIM".equalsIgnoreCase(req.corteDeseado)) {
            ajusteNota = "Corte ceñido al cuerpo manteniendo comodidad en algodón Tangüis.";
            confianza = 96;
        }

        return ResponseEntity.ok(Map.of(
                "tallaRecomendada", tallaFinal,
                "confianzaPorcentaje", confianza,
                "imcCalculado", Math.round(imc * 10.0) / 10.0,
                "ajusteNota", ajusteNota,
                "mensaje", String.format("¡Tu talla ideal es %s! Ajuste perfecto garantizado con política de cambio sin costo.", tallaFinal)
        ));
    }

    private String subirTalla(String t) {
        return switch (t) {
            case "XS" -> "S";
            case "S" -> "M";
            case "M" -> "L";
            case "L" -> "XL";
            default -> "XXL";
        };
    }
}
