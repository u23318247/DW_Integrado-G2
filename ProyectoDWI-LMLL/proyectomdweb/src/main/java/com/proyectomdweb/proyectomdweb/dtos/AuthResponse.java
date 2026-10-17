package com.proyectomdweb.proyectomdweb.dtos;

import java.util.List;

public record AuthResponse(
    String token,
    String tipo,
    String username,
    String email,
    List<String> roles
) {
    public AuthResponse(String token, String username, String email, List<String> roles) {
        this(token, "Bearer", username, email, roles);
    }
}
