package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_liquidacion")
public class DetalleLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dtliq_id_int")
    private Long id;

    @Column(name = "dtliq_tick_vac")
    private String ticket;

    @Column(name = "dtliq_cost_tick_dc")
    private BigDecimal costoTicket;

    @Column(name = "dtliq_carg_serv_dc")
    private BigDecimal cargoServicio;

    @Column(name = "dtliq_val_vent_dc")
    private BigDecimal valorVenta;

    @Column(name = "dtliq_fac_comp_vac")
    private String facturaCompra;

    @Column(name = "dtliq_bol_fac_pasj_vac")
    private String boletaPasajero;

    @Column(name = "dtliq_mont_desct_dc")
    private BigDecimal montoDescuento;

    @Column(name = "dtliq_pag_pax_dol_dc")
    private BigDecimal pagoPaxUSD;

    @Column(name = "dtliq_pag_pax_sol_dc")
    private BigDecimal pagoPaxPEN;

    @Column(name = "dtliq_fec_cre_tmp")
    private LocalDateTime creado;

    @Column(name = "dtliq_fec_upd_tmp")
    private LocalDateTime actualizado;

    @Column(name = "opr_id_int")
    private Long operadorId;

    @Column(name = "prov_id_int")
    private Long proveedorId;

    @Column(name = "via_id_int")
    private Long viajeroId;

    @Column(name = "prod_id_int")
    private Long productoId;

    @Column(name = "liq_id_int")
    private Long liquidacionId;
}
