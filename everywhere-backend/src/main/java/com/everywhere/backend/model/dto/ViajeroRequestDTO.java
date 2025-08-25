package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.time.LocalDate;

@Data
public class ViajeroRequestDTO {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaEmisionDocumento;
    private LocalDate fechaVencimientoDocumento;

    @Valid
    private PersonaRequestDTO persona;
}
