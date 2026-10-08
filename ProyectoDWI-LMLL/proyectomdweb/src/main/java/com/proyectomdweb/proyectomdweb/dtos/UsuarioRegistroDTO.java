package com.proyectomdweb.proyectomdweb.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email   (message = "Formato de correo inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size    (min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // Opcionales (no llevan @NotBlank)
    private String telefono;
    private String direccion;
}
