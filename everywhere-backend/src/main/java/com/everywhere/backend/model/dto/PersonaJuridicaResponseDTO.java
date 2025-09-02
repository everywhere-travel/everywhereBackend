package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonaJuridicaResponseDTO {
    private Integer id;
    private String ruc;
    private String razonSocial;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private PersonaResponseDTO persona;
}
