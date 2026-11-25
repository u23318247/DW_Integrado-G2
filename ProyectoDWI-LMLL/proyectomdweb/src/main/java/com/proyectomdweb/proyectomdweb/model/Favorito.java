package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favoritos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_clave", "producto_id"})
})
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_clave", nullable = false, length = 120)
    private String usuarioClave;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;
}
