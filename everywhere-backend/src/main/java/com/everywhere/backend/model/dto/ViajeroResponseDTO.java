package com.everywhere.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ViajeroResponseDTO {
    private Integer id;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    
    @JsonManagedReference("viajero-personaNatural")
    private PersonaNaturalResponseDTO personaNatural;
}