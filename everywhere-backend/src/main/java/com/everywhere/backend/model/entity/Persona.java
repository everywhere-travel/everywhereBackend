package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id_int")
    private Long id;

    @Column(name = "per_email_vac", length = 150)
    private String email;

    @Column(name = "per_telf_vac", length = 50)
    private String telefono;

    @Column(name = "per_direc_vac", length = 200)
    private String direccion;

    @Column(name = "per_obs_vac", length = 250)
    private String observacion;

    @Column(name = "per_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "per_upd_tmp")
    private LocalDateTime actualizado;
}
