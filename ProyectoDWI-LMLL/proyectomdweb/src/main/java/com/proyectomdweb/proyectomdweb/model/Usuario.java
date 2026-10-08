package com.proyectomdweb.proyectomdweb.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 12)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(length = 20)
    private String rol; // 'ROLE_ADMIN', 'ROLE_VENDEDOR', 'ROLE_CLIENTE'

    //*-- MÉTODOS OBLIGATORIOS DE USERDETAILS (Spring Security) --*//

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Rol rolEnum = Rol.desdeTexto(this.rol, this.email);
        return switch (rolEnum) {
            case ROLE_ADMIN -> List.of(new SimpleGrantedAuthority(Rol.ROLE_ADMIN.name()), new SimpleGrantedAuthority("ROLE_USER"));
            case ROLE_VENDEDOR -> List.of(new SimpleGrantedAuthority(Rol.ROLE_VENDEDOR.name()), new SimpleGrantedAuthority("ROLE_USER"));
            case ROLE_CLIENTE -> List.of(new SimpleGrantedAuthority(Rol.ROLE_CLIENTE.name()), new SimpleGrantedAuthority("ROLE_USER"));
        };
    }
    
    @Override
    public String getUsername() {
        return this.email; // Se usa el email como username para el Login
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // El usuario está activo
    }
}
