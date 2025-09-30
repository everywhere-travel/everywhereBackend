package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "viajeros")
public class Viajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "via_id_int")
    private Integer id;

    @Column(name = "via_nomb_vac")
    private String nombres;

    @Column(name = "via_ap_pat_vac")
    private String apellidoPaterno;

    @Column(name = "via_ap_mat_vac")
    private String apellidoMaterno;

    @Column(name = "via_fec_nac_tmp")
    private LocalDate fechaNacimiento;

    @Column(name = "via_nacio_vac")
    private String nacionalidad;

    @Column(name = "via_resi_vac")
    private String residencia;

    @Column(name = "via_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "via_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "per_id_int", nullable = false)
    private Personas personas;

}
