package com.proyectomdweb.proyectomdweb.model;

public enum Rol {
    ROLE_CLIENTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN;

    public static Rol desdeTexto(String valor, String email) {
        if ("admin@email.com".equalsIgnoreCase(email) || "ROLE_ADMIN".equalsIgnoreCase(valor) || "ADMIN".equalsIgnoreCase(valor)) {
            return ROLE_ADMIN;
        }
        if ("ROLE_VENDEDOR".equalsIgnoreCase(valor) || "VENDEDOR".equalsIgnoreCase(valor)) {
            return ROLE_VENDEDOR;
        }
        return ROLE_CLIENTE;
    }
}
