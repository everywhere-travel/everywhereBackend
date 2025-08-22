package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "persona_natural")
public class PersonaNatural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_nat_id_int")
    private Long id;

    @Column(name = "per_nat_doc_int")
    private String documento;

    @Column(name = "per_nat_nomb_vac", length = 150)
    private String nombres;

    @Column(name = "per_nat_apell_vac", length = 150)
    private String apellidos;

    @Column(name = "per_nat_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "per_nat_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "per_id_int", nullable = false)
    private Persona persona;
}
