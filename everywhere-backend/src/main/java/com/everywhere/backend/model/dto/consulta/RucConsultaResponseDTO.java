package com.everywhere.backend.model.dto.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RucConsultaResponseDTO {
    private String ruc;
    private String razonSocial;
    private String estado;
    private String condicion;
    private String direccion;
    private String departamento;
    private String origen; // "LOCAL" o "APIPERU"
    private boolean encontrado;
    private Integer personaJuridicaId;
    private Integer personaId;
    private String mensaje;
}
