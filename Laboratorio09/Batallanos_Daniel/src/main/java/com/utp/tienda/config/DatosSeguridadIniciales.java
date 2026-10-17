package com.utp.tienda.config;

import com.utp.tienda.model.Rol;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.RolRepository;
import com.utp.tienda.repository.UsuarioRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tarea 6 - Poblado semilla de datos de seguridad.
 *
 * <p>Inserta los roles y usuarios de prueba <b>solo si no existen</b>, de modo que
 * el proceso sea idempotente y se pueda ejecutar en cada arranque. Las contrasenas
 * se guardan codificadas con BCrypt, nunca en texto plano.</p>
 *
 * <p>Usuarios creados:</p>
 * <ul>
 *   <li>{@code usuario} / {@code Usuario123*} -&gt; ROLE_USER (lectura)</li>
 *   <li>{@code admin} / {@code Admin123*} -&gt; ROLE_ADMIN, ROLE_USER (control total)</li>
 * </ul>
 */
@Configuration
public class DatosSeguridadIniciales {

    private static final Logger log = LoggerFactory.getLogger(DatosSeguridadIniciales.class);

    public static final String USUARIO_USER = "usuario";
    public static final String CLAVE_USER = "Usuario123*";
    public static final String USUARIO_ADMIN = "admin";
    public static final String CLAVE_ADMIN = "Admin123*";

    @Bean
    public CommandLineRunner inicializarDatosSeguridad(RolRepository rolRepository,
                                                      UsuarioRepository usuarioRepository,
                                                      PasswordEncoder passwordEncoder) {
        return args -> sembrar(rolRepository, usuarioRepository, passwordEncoder);
    }

    @Transactional
    void sembrar(RolRepository rolRepository,
                 UsuarioRepository usuarioRepository,
                 PasswordEncoder passwordEncoder) {

        Rol rolUser = obtenerOCrearRol(rolRepository, "ROLE_USER");
        Rol rolAdmin = obtenerOCrearRol(rolRepository, "ROLE_ADMIN");

        crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder,
                USUARIO_USER, CLAVE_USER, Set.of(rolUser));

        Set<Rol> rolesAdmin = new LinkedHashSet<>();
        rolesAdmin.add(rolAdmin);
        rolesAdmin.add(rolUser);
        crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder,
                USUARIO_ADMIN, CLAVE_ADMIN, rolesAdmin);

        log.info("Datos de seguridad listos: usuario '{}' (ROLE_USER) y admin '{}' (ROLE_ADMIN, ROLE_USER)",
                USUARIO_USER, USUARIO_ADMIN);
    }

    private Rol obtenerOCrearRol(RolRepository rolRepository, String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = rolRepository.save(new Rol(nombre));
            log.info("Rol creado: {}", nombre);
            return rol;
        });
    }

    private void crearUsuarioSiNoExiste(UsuarioRepository usuarioRepository,
                                        PasswordEncoder passwordEncoder,
                                        String username, String passwordPlano, Set<Rol> roles) {
        if (usuarioRepository.existsByUsername(username)) {
            log.info("Usuario '{}' ya existe: se conserva el existente", username);
            return;
        }
        Usuario usuario = new Usuario(username, passwordEncoder.encode(passwordPlano), true);
        roles.forEach(usuario::agregarRol);
        usuarioRepository.save(usuario);
        log.info("Usuario creado: '{}' con roles {}", username, usuario.nombresRoles());
    }
}