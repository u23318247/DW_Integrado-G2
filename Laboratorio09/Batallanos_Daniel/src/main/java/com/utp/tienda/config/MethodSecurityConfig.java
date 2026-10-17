package com.utp.tienda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Tarea 9.2 - Seguridad a nivel de metodo (reto avanzado).
 *
 * <p>{@code @EnableMethodSecurity} activa el interceptor AOP que evalua las
 * anotaciones {@code @PreAuthorize}, {@code @PostAuthorize}, {@code @Secured}
 * y {@code @RolesAllowed} sobre los beans de Spring.</p>
 *
 * <p>Se combina con las reglas de URL del {@code SecurityFilterChain}: la URL decide
 * si la peticion llega al controlador y el metodo vuelve a validar la regla
 * critica en la capa de servicio (por ejemplo {@code eliminarProducto}).</p>
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}