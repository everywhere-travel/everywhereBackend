package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonaNaturalResponseDTO {
    private Integer id;
    private String documento;
    private String nombres;
    private String apellidos;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private PersonaResponseDTO persona;
}
