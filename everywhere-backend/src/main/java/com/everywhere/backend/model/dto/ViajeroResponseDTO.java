package com.everywhere.backend.model.dto;

import lombok.Builder;
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
    private PersonaNaturalResumenDTO personaNatural;

    @Data
    @Builder
    public static class PersonaNaturalResumenDTO {
        private Integer id;
        private String nombres;
        private String apellidosPaterno;
        private String apellidosMaterno;
        private String documento;
    }
}