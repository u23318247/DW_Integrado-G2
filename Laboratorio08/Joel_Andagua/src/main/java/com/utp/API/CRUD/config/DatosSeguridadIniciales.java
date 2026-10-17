package com.utp.API.CRUD.config;

import com.utp.API.CRUD.model.Rol;
import com.utp.API.CRUD.model.Usuario;
import com.utp.API.CRUD.repository.RolRepository;
import com.utp.API.CRUD.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
public class DatosSeguridadIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                    .orElseGet(() -> rolRepository.save(new Rol("ROLE_USER")));
            Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> rolRepository.save(new Rol("ROLE_ADMIN")));

            if (usuarioRepository.findByUsername("usuario").isEmpty()) {
                Usuario usuario = new Usuario(
                        "usuario",
                        passwordEncoder.encode("Usuario123*"),
                        true
                );
                usuario.setRoles(Set.of(rolUser));
                usuarioRepository.save(usuario);
            }

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario(
                        "admin",
                        passwordEncoder.encode("Admin123*"),
                        true
                );
                admin.setRoles(Set.of(rolAdmin, rolUser));
                usuarioRepository.save(admin);
            }
        };
    }
}