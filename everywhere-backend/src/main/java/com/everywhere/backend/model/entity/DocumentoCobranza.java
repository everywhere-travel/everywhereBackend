package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documento_cobranza")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_co_id_int")
    private Long id;

    @Column(name = "doc_co_num_vac")
    private String numero;

    @Column(name = "doc_co_fec_emi_tmp")
    private LocalDateTime fechaEmision;

    @Column(name = "doc_co_obs_vac", columnDefinition = "TEXT")
    private String observaciones;

    // Relaciones con otras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carp_id_int")
    private Carpeta carpeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_int")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suc_id_int")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id_int")
    private Personas persona;

    // Relación con detalles
    @OneToMany(mappedBy = "documentoCobranza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DetalleDocumentoCobranza> detalles;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cot_id_int", unique = true)
    private Cotizacion cotizacion;

    @Column(name = "doc_co_nro_ser_vac", length = 50)
    private String nroSerie;

    @Column(name = "doc_co_file_ven_vac", length = 100)
    private String fileVenta;

    @Column(name = "doc_co_cos_env_dc")
    private Double costoEnvio;

    @Column(name = "doc_co_mon_vac", length = 10)
    private String moneda;
}
