package com.proyectomdweb.proyectomdweb.model;

public enum Rol {
    ROLE_CLIENTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN,
    ROLE_ALMACENERO,
    ROLE_REPARTIDOR;

    public static Rol desdeTexto(String valor, String email) {
        if ("admin@email.com".equalsIgnoreCase(email) || "ROLE_ADMIN".equalsIgnoreCase(valor) || "ADMIN".equalsIgnoreCase(valor)) {
            return ROLE_ADMIN;
        }
        if ("almacen@email.com".equalsIgnoreCase(email) || "ROLE_ALMACENERO".equalsIgnoreCase(valor) || "ALMACENERO".equalsIgnoreCase(valor)) {
            return ROLE_ALMACENERO;
        }
        if ("repartidor@email.com".equalsIgnoreCase(email) || "ROLE_REPARTIDOR".equalsIgnoreCase(valor) || "REPARTIDOR".equalsIgnoreCase(valor)) {
            return ROLE_REPARTIDOR;
        }
        if ("ROLE_VENDEDOR".equalsIgnoreCase(valor) || "VENDEDOR".equalsIgnoreCase(valor)) {
            return ROLE_VENDEDOR;
        }
        return ROLE_CLIENTE;
    }
}
