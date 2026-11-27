package com.proyectomdweb.proyectomdweb.model;

public enum Rol {
    ROLE_CLIENTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN,
    ROLE_ALMACENERO,
    ROLE_REPARTIDOR;

    /**
     * Convierte el valor de la columna usuarios.rol en un Rol.
     * El rol depende SOLO de lo guardado en la base de datos, nunca del email:
     * así nadie obtiene privilegios por registrarse con un correo concreto.
     * Acepta "ROLE_ADMIN" o "ADMIN"; un valor vacío o desconocido es ROLE_CLIENTE.
     */
    public static Rol desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return ROLE_CLIENTE;
        }
        String normalizado = valor.trim().toUpperCase();
        if (!normalizado.startsWith("ROLE_")) {
            normalizado = "ROLE_" + normalizado;
        }
        for (Rol rol : values()) {
            if (rol.name().equals(normalizado)) {
                return rol;
            }
        }
        return ROLE_CLIENTE;
    }
}
