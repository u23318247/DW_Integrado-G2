package com.proyectomdweb.proyectomdweb.controller.api;

import com.proyectomdweb.proyectomdweb.model.Cupon;
import com.proyectomdweb.proyectomdweb.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
public class ApiCuponController {

    private final CuponRepository cuponRepository;

    @GetMapping
    public ResponseEntity<List<Cupon>> listarActivos() {
        return ResponseEntity.ok(cuponRepository.findByActivoTrue());
    }

    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(
            @RequestParam String codigo,
            @RequestParam(defaultValue = "0") BigDecimal subtotal) {

        var opt = cuponRepository.findByCodigoIgnoreCase(codigo.trim());
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "mensaje", "Cupón no encontrado."));
        }
        Cupon c = opt.get();
        if (!Boolean.TRUE.equals(c.getActivo())) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "mensaje", "El cupón está inactivo."));
        }
        if (c.getFechaExpiracion() != null && c.getFechaExpiracion().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "mensaje", "El cupón ha expirado."));
        }
        if (c.getUsosMaximos() != null && c.getUsosActuales() != null && c.getUsosActuales() >= c.getUsosMaximos()) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "mensaje", "El cupón alcanzó su límite de usos."));
        }
        if (subtotal.compareTo(c.getMontoMinimo()) < 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valido", false,
                    "mensaje", "El monto mínimo para este cupón es S/ " + c.getMontoMinimo()
            ));
        }

        BigDecimal descuento = subtotal.multiply(c.getPorcentajeDescuento())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalFinal = subtotal.subtract(descuento).max(BigDecimal.ZERO);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("valido", true);
        res.put("codigo", c.getCodigo());
        res.put("porcentajeDescuento", c.getPorcentajeDescuento());
        res.put("descuento", descuento);
        res.put("totalConDescuento", totalFinal);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<Cupon> crear(@RequestBody Cupon cupon) {
        if (cupon.getActivo() == null) cupon.setActivo(true);
        if (cupon.getUsosActuales() == null) cupon.setUsosActuales(0);
        if (cupon.getMontoMinimo() == null) cupon.setMontoMinimo(BigDecimal.ZERO);
        cupon.setCodigo(cupon.getCodigo().trim().toUpperCase());
        return ResponseEntity.ok(cuponRepository.save(cupon));
    }
}
