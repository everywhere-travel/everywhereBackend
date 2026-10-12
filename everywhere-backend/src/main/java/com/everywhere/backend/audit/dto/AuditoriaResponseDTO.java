package com.everywhere.backend.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponseDTO {
    private Long id;
    private String tabla;
    private String registroId;
    private String accion;
    private Integer usuarioId;
    private String username;
    private String usuarioNombre;
    private String ipOrigen;
    private String endpoint;
    private LocalDateTime fechaHora;
    private boolean tieneDiff;
}
