package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="telefonos_personas")
public class TelefonoPersona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tel_id_int")
    private Integer id;

    @Column(name = "tel_num_vac", length = 20)
    private String numero;

    @Column(name = "tel_cod_vac", length = 5)
    private String codigoPais;

    @Column(name = "tel_tipo_vac", length = 15)
    private String tipo;

    @Column(name = "tel_desc_vac", length = 100)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id_int")
    @JsonBackReference
    private Personas persona;

}