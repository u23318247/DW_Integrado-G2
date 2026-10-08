package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //* Relación OneToOne porque pedido_id es UNIQUE *//
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie_correlativo", nullable = false, length = 20)
    private String serieCorrelativo;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal total;
}
