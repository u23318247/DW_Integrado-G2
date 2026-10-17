package com.utp.tienda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entidad {@code Rol} mapeada a la tabla {@code roles}.
 *
 * <p>Convencion obligatoria: el nombre del rol SIEMPRE lleva el prefijo
 * {@code ROLE_} (por ejemplo {@code ROLE_USER}, {@code ROLE_ADMIN}). Spring Security
 * usa ese prefijo para resolver las expresiones {@code hasRole(...)} y
 * {@code hasAnyRole(...)}.</p>
 */
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    public Rol() {
        // Constructor vacio requerido por JPA
    }

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * equals/hashCode basado en la clave natural ({@code nombre}) y no en el id:
     * durante el sembrado inicial los roles todavia no tienen id asignado, y dos roles
     * distintos (ROLE_USER / ROLE_ADMIN) deben seguir considerandose distintos
     * dentro de un Set.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rol otro)) {
            return false;
        }
        return Objects.equals(this.nombre, otro.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nombre);
    }

    @Override
    public String toString() {
        return "Rol{id=" + id + ", nombre='" + nombre + "'}";
    }
}