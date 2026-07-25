package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CotizacionRequestDto {
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombreCotizacion;
    @Min(value = 0, message = "La cantidad de adultos no puede ser negativa")
    @Max(value = 1000, message = "La cantidad de adultos no puede superar 1000")
    private Integer cantAdultos;
    @Min(value = 0, message = "La cantidad de niños no puede ser negativa")
    @Max(value = 1000, message = "La cantidad de niños no puede superar 1000")
    private Integer cantNinos;
    private LocalDateTime fechaVencimiento;
    @Size(max = 500, message = "El origen-destino no puede superar 500 caracteres")
    private String origenDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    @Size(max = 50, message = "La moneda no puede superar 50 caracteres")
    private String moneda;
    @Size(max = 10000, message = "La observación no puede superar 10000 caracteres")
    private String observacion;

    private Integer counterId;
    private Integer formaPagoId;
    private Integer estadoCotizacionId;
    private Integer sucursalId;
    private Integer carpetaId;
    private Integer personaId;
}
