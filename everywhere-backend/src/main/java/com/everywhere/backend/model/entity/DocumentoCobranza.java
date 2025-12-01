package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "documento_cobranza")
@Data
public class DocumentoCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_co_id_int")
    private Long id;

    @Column(name = "doc_co_num_vac")
    private String numero;

    @CreationTimestamp
    @Column(name = "doc_co_fec_emi_tmp")
    private LocalDateTime fechaEmision;

    @Column(name = "doc_co_obs_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "carp_id_int")
    private Carpeta carpeta;

    @ManyToOne
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "usr_id_int")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "suc_id_int")
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "per_id_int")
    private Personas persona;

    @ManyToOne
    @JoinColumn(name = "per_jur_id_int")
    private PersonaJuridica personaJuridica;

    @ManyToOne
    @JoinColumn(name = "dtdoc_id_int")
    private DetalleDocumento detalleDocumento;

    @OneToMany(mappedBy = "documentoCobranza", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetalleDocumentoCobranza> detalles;

    @OneToOne
    @JoinColumn(name = "cot_id_int", unique = true)
    private Cotizacion cotizacion;

    @Column(name = "doc_co_file_ven_vac")
    private String fileVenta;

    @Column(name = "doc_co_cos_env_dc")
    private Double costoEnvio;

    @Column(name = "doc_co_mon_vac")
    private String moneda;
}