package com.everywhere.backend.model.dto.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DniConsultaResponseDTO {
    private String dni;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto;
    private Integer codigoVerificacion;
    private String origen; // "LOCAL" o "APIPERU"
    private boolean encontrado;
    private Integer personaNaturalId;
    private Integer personaId;
    private String mensaje;
}
