package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.Viajero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViajeroFrecuenteResponseDTO {

    private Integer id;
    private String areolinea;
    private String codigo;
    private Viajero viajero;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
