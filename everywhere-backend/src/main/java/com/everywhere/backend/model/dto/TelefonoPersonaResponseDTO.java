package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class TelefonoPersonaResponseDTO {

    private Integer id;
    private String numero;
    private String codigoPais;
    private String tipo;
    private String descripcion;
    private Integer personaId;
}
