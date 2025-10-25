package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "personas")
public class Personas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id_int")
    private Integer id;

    @Column(name = "per_email_vac")
    private String email;

    @Column(name = "per_telf_vac")
    private String telefono;

    @Column(name = "per_direc_vac")
    private String direccion;

    @Column(name = "per_obs_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_per_id_int")
    private CategoriaPersona categoriaPersona;

    @CreationTimestamp
    @Column(name = "per_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "per_upd_tmp")
    private LocalDateTime actualizado;
}