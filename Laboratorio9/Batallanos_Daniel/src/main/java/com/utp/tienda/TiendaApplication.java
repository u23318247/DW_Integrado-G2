package com.utp.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de productos e inventario.
 *
 * <p>Laboratorio 09 - Spring Security: Roles y Permisos.
 * La aplicacion es stateless (sin sesiones) y se protege con HTTP Basic.</p>
 */
@SpringBootApplication
public class TiendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaApplication.class, args);
    }
}