package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personas_cotizaciones")
@Data
public class PersonaCotizacion {

    @Id
    @Column(name = "per_id_int")
    private Long personaId;

    @Id
    @Column(name = "cot_id_int")
    private Long cotizacionId;
}
