package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "doc_id_int" )
    private Integer id;

    @Column(name = "doc_tipo_vac", nullable = false, unique = true)
    private String tipo;

    @Lob
    @Column(name = "doc_desc_vac", nullable = false)
    private String descripcion;

    @Column(name = "doc_cre_tmp", updatable = false)
    private java.time.LocalDateTime creado;

    @Column(name = "doc_upd_tmp")
    private LocalDateTime actualizado;
}
