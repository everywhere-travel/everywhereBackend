package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name= "detalle_documento")
public class DetalleDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dtdoc_id_int")
    private Integer id;

    @Column(name = "dtdoc_numero_vac", nullable = false)
    private String numero;

    @Column(name = "dtdoc_fec_emi_tmp", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "dtdoc_fec_ven_tmp", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "dtdoc_ori_vac" , nullable = false)
    private String origen;

    @ManyToOne
    @JoinColumn(name = "doc_id_int", nullable = false)
    private Documento documento;

    @ManyToOne
    @JoinColumn(name = "via_id_int", nullable = false)
    private Viajero viajero;

}
