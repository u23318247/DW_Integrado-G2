package com.utp.tienda.config;

import com.utp.tienda.model.Producto;
import com.utp.tienda.model.Rol;
import com.utp.tienda.model.Usuario;
import com.utp.tienda.repository.ProductoRepository;
import com.utp.tienda.repository.RolRepository;
import com.utp.tienda.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@Profile("!test")
public class DatosSeguridadIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosSeguridadIniciales(UsuarioRepository usuarioRepository,
                                  RolRepository rolRepository,
                                  ProductoRepository productoRepository,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_ADMIN")));

        Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_USER")));

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActivo(true);
            admin.setRoles(Set.of(rolAdmin, rolUser));
            usuarioRepository.save(admin);
        }

        if (usuarioRepository.findByUsername("usuario").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("usuario");
            usuario.setPassword(passwordEncoder.encode("user123"));
            usuario.setActivo(true);
            usuario.setRoles(Set.of(rolUser));
            usuarioRepository.save(usuario);
        }

        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto("Laptop Pro 15", "Tecnología", new BigDecimal("3500.00"), 10));
            productoRepository.save(new Producto("Teclado Mecánico RGB", "Accesorios", new BigDecimal("250.00"), 3));
            productoRepository.save(new Producto("Monitor 27 Pulgadas", "Pantallas", new BigDecimal("980.00"), 4));
            productoRepository.save(new Producto("Mouse Ergonómico", "Accesorios", new BigDecimal("120.00"), 25));
            productoRepository.save(new Producto("Auriculares Gamer", "Audio", new BigDecimal("320.00"), 12));
        }
    }
}
