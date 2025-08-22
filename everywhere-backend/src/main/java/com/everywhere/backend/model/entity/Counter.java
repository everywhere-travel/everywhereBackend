package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "counter")
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cou_id_int")
    private Long id;

    @Column(name = "cou_nom_vac", length = 150)
    private String nombre;

    @Column(name = "cou_est_bol")
    private Boolean estado;
}
