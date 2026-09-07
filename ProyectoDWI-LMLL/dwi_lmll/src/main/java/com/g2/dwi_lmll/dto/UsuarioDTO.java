package com.g2.dwi_lmll.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long          id;
    private String        nombre;
    private String        email;
    private String        telefono;
    private String        direccion;
    private LocalDateTime fechaRegistro;
}
