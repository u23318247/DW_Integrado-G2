package com.g2.dwi_lmll.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //* Relación: MUCHOS productos pertenecen a UNA categoría *//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id") // Esta es la llave foránea
    private Categoria categoria;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String genero;

    @Column(name = "imagen_url", nullable = false, length = 255)
    private String imagenUrl;

    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase;

    @Column(columnDefinition = "boolean default true")
    private Boolean disponibilidad;

}
