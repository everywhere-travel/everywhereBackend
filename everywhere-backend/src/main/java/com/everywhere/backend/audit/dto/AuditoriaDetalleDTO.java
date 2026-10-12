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
public class AuditoriaDetalleDTO {
    private Long id;
    private String tabla;
    private String registroId;
    private String accion;
    private Integer usuarioId;
    private String username;
    private String usuarioNombre;
    private String datosPrevios;
    private String datosNuevos;
    private String cambiosDiff;
    private String ipOrigen;
    private String endpoint;
    private String userAgent;
    private LocalDateTime fechaHora;
}
