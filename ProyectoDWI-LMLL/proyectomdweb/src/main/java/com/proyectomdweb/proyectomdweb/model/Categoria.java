package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nombre;

    // Una categoría tiene MUCHOS productos
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY) 
    @ToString.Exclude 
    private List<Producto> productos;
}