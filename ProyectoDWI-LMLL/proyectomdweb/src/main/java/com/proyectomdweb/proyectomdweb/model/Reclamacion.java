package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Libro de Reclamaciones Virtual
 * Conforme a la Ley N.° 29571 (Código de Protección y Defensa del Consumidor - INDECOPI)
 * Alumna: Antonela Jaimes
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reclamaciones")
@EntityListeners(AuditingEntityListener.class)
public class Reclamacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_reclamo", unique = true, nullable = false, length = 30)
    private String codigoReclamo;

    @Column(nullable = false, length = 20)
    private String tipo; // RECLAMO o QUEJA

    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "nombres_apellidos", nullable = false, length = 150)
    private String nombresApellidos;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(name = "tipo_bien", nullable = false, length = 20)
    private String tipoBien; // PRODUCTO o SERVICIO

    @Column(name = "monto_reclamado")
    private Double montoReclamado;

    @Column(name = "descripcion_bien", nullable = false, length = 255)
    private String descripcionBien;

    @Column(name = "detalle_reclamo", nullable = false, columnDefinition = "TEXT")
    private String detalleReclamo;

    @Column(name = "pedido_consumidor", nullable = false, columnDefinition = "TEXT")
    private String pedidoConsumidor;

    @CreatedDate
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "PENDIENTE"; // PENDIENTE, EN_REVISION, ATENDIDO

    @Column(name = "respuesta_proveedor", columnDefinition = "TEXT")
    private String respuestaProveedor;
}
