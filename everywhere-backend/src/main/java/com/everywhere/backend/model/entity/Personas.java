package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "personas")
public class Personas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id_int")
    private Integer id;

    @Column(name = "per_email_vac", length = 150)
    private String email;

    @Column(name = "per_telf_vac", length = 50)
    private String telefono;

    @Column(name = "per_direc_vac", length = 200)
    private String direccion;

    @Column(name = "per_obs_vac", length = 250)
    private String observacion;

    @CreationTimestamp
    @Column(name = "per_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "per_upd_tmp")
    private LocalDateTime actualizado;
}
