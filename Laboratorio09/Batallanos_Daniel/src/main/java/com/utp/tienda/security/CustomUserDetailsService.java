package com.utp.tienda.security;

import com.utp.tienda.model.Rol;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Tarea 4 - Servicio de carga de usuarios que conecta MySQL con Spring Security.
 *
 * <p>Autentica contra la tabla {@code usuarios} y convierte los roles de la entidad
 * {@link Usuario} en autoridades de Spring Security. Los nombres de rol ya vienen
 * con el prefijo {@code ROLE_} desde la base de datos, por eso se copian tal cual.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Busca el usuario por nombre y construye el {@link UserDetails} con sus autoridades.
     *
     * @param username nombre de usuario enviado en el encabezado HTTP Basic
     * @return usuario de Spring Security listo para autenticar
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Los nombres de rol se copian tal cual (ya incluyen el prefijo ROLE_)
        String[] autoridades = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .sorted()
                .toArray(String[]::new);

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(autoridades)
                .disabled(!usuario.isActivo())
                .build();
    }
}