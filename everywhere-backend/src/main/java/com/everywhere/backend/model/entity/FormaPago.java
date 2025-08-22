package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forma_pago")
public class FormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id_int")
    private Long id;

    @Column(name = "form_cod_int")
    private Integer codigo;

    @Column(name = "form_desc_vac", length = 200)
    private String descripcion;
}
