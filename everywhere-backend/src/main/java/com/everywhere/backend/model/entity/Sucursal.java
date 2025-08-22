package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suc_id_int")
    private Long id;

    @Column(name = "suc_desc_vac", length = 200)
    private String descripcion;

    @Transient
    private Boolean estado;
}
