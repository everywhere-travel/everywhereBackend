package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carpetas")
public class Carpeta {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name="carp_id_int" )
    private Long id;

    @Column(name="carp_nom_vac", length=100 )
    private String nombre;

    @Column (name = "carp_desc_vac", length=300 )
    private String descripcion;

    @Column(name="carp_cre_tmp" )
    private LocalDateTime creado;

    @Column(name="carp_upd_tmp" )
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "car´_id_padr_int")
    private Carpeta carpetaPadre;

}
