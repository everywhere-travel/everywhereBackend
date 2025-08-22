package com.everywhere.backend.model.entity;
import java.io.Serializable;
import lombok.Data;

@Data
public class PersonaCotizacionId implements Serializable {
    private Long persona;
    private Long cotizacion;
}