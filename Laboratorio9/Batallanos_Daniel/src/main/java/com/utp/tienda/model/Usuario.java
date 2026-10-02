package com.utp.tienda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Entidad {@code Usuario} mapeada a la tabla {@code usuarios}.
 *
 * <p>La contrasena SIEMPRE se almacena como hash BCrypt (prefijo {@code $2a$},
 * {@code $2b$} o {@code $2y$}); jamas en texto plano. El campo se marca con
 * {@link JsonIgnore} como segunda barrera para que ningun endpoint exponga
 * credenciales aunque la entidad se serialice por accidente.</p>
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 80)
    private String username;

    /** Hash BCrypt de la contrasena (longitud 60 caracteres, margen 100). */
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new LinkedHashSet<>();

    public Usuario() {
        // Constructor vacio requerido por JPA
    }

    public Usuario(String username, String password) {
        this(username, password, true);
    }

    public Usuario(String username, String password, boolean activo) {
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    public Usuario(String username, String password, Set<Rol> roles) {
        this(username, password, true);
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public void agregarRol(Rol rol) {
        if (rol != null) {
            this.roles.add(rol);
        }
    }

    /** Nombres de los roles asignados (ya Incluyen el prefijo ROLE_). */
    public Set<String> nombresRoles() {
        Set<String> nombres = new TreeSet<>();
        for (Rol rol : this.roles) {
            nombres.add(rol.getNombre());
        }
        return nombres;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Rol> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles == null ? new LinkedHashSet<>() : new LinkedHashSet<>(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario otro)) {
            return false;
        }
        return Objects.equals(this.username, otro.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username);
    }

    /** Nunca imprime la contrasena. */
    @Override
    public String toString() {
        return "Usuario{id=" + id + ", username='" + username + "', activo=" + activo
                + ", roles=" + nombresRoles() + "}";
    }
}