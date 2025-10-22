package com.everywhere.backend.model.dto;

import lombok.Data; 
import jakarta.validation.Valid;

@Data
public class PersonaNaturalRequestDTO {
    private String documento;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String sexo; 

    @Valid
    private PersonaRequestDTO persona;
}
