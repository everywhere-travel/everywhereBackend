package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PersonaResponseDTO {
    private Integer id;
    private String email;
    private String telefono;
    private String direccion;
    private String observacion;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private List<TelefonoPersonaResponseDTO> telefonos;
    private List<CorreoPersonaResponseDTO> correos;
}