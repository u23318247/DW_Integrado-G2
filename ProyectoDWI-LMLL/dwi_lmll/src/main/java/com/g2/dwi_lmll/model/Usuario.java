package com.g2.dwi_lmll.model;

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

    //*-- MÉTODOS OBLIGATORIOS DE USERDETAILS (Spring Security) --*//

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Si el correo es del admin, se le asigna doble rol
        if ("admin@email.com".equals(this.email)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } 
        // Se asigna el rol 'ROLE_USER' por defecto a todos los que se logueen.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        
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
