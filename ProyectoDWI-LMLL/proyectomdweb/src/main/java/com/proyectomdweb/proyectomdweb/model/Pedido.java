package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; 

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoPedido estado;

    @Column(name = "codigo_entrega", length = 10)
    private String codigoEntrega; // PIN o código secreto para validar entrega

    @Column(name = "direccion_envio", length = 255)
    private String direccionEnvio;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;
}
