package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cupones")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "monto_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoMinimo;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Column(name = "usos_actuales")
    private Integer usosActuales;
}
