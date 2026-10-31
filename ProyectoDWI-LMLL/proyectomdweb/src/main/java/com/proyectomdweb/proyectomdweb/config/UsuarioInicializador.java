package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.model.Usuario;
import com.proyectomdweb.proyectomdweb.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inicializador idempotente de usuarios base y roles del sistema:
 * - ROLE_ADMIN: admin@email.com / admin123
 * - ROLE_VENDEDOR: vendedor@email.com / admin123
 * - ROLE_ALMACENERO: almacen@email.com / admin123
 * - ROLE_REPARTIDOR: repartidor@email.com / admin123
 * - ROLE_CLIENTE: cliente@email.com / admin123
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class UsuarioInicializador implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        crearUsuarioSiFalta("Admin General", "admin@email.com", "admin123", "ROLE_ADMIN", "Av. Central 123, Lima", "919191919", "70000001");
        crearUsuarioSiFalta("Vendedor Oficial", "vendedor@email.com", "admin123", "ROLE_VENDEDOR", "Gamarra 456, La Victoria", "987654321", "70000002");
        crearUsuarioSiFalta("Almacenero Central", "almacen@email.com", "admin123", "ROLE_ALMACENERO", "Almacén Central Lote 4, Lima", "955443322", "70000003");
        crearUsuarioSiFalta("Repartidor Expreso", "repartidor@email.com", "admin123", "ROLE_REPARTIDOR", "Base de Despacho 12, Lima", "911223344", "70000004");
        crearUsuarioSiFalta("Antonela Jaimes", "cliente@email.com", "admin123", "ROLE_CLIENTE", "Av. Larco 789, Miraflores", "933221100", "72345678");
    }

    private void crearUsuarioSiFalta(String nombre, String email, String password, String rol, String direccion, String telefono, String dni) {
        var opt = usuarioRepository.findByEmail(email);
        if (opt.isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setRol(rol);
            u.setDireccion(direccion);
            u.setTelefono(telefono);
            u.setDni(dni);
            usuarioRepository.save(u);
            log.info("Usuario inicial asegurado: {} ({}) con DNI {}", email, rol, dni);
        } else {
            Usuario exist = opt.get();
            if (exist.getDni() == null || exist.getDni().isBlank()) {
                exist.setDni(dni);
                usuarioRepository.save(exist);
            }
        }
    }
}
