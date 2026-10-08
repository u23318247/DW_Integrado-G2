package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto_detalles")
public class ProductoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //* Relación: MUCHOS detalles pertenecen a UN producto general *//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "nombre_completo", nullable = false, length = 70)
    private String nombreCompleto;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo; // SKU

    @Column(name = "imagenUrl", nullable = false, length = 255)
    private String imagenUrl;

    @Column(nullable = false, length = 20)
    private String marca;

    @Column(nullable = false, length = 10)
    private String talla;

    @Column(nullable = false, length = 20)
    private String color;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(columnDefinition = "int default 0")
    private Integer stock;

    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase;

    @Column(name = "precio_adicional", columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal precioAdicional;
}
