package com.utp.tienda.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.utp.tienda.model.Rol;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.UsuarioRepository;

/**
 * Traduce un Usuario de MySQL a UserDetails, el contrato que entiende Spring Security.
 * Cada Rol se convierte en una GrantedAuthority (ROLE_USER, ROLE_ADMIN).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String[] autoridades = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .toArray(String[]::new);

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(autoridades)
                .disabled(!usuario.isActivo())
                .build();
    }
}
