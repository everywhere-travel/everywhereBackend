package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.AsientoContableMapper;
import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;
import com.everywhere.backend.model.dto.DetalleAsientoContableRequestDTO;
import com.everywhere.backend.model.entity.AsientoContable;
import com.everywhere.backend.model.entity.CuentaContable;
import com.everywhere.backend.model.entity.DetalleAsientoContable;
import com.everywhere.backend.model.entity.PagoPax;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.Recibo;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.repository.AsientoContableRepository;
import com.everywhere.backend.repository.CuentaContableRepository;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.DetalleReciboRepository;
import com.everywhere.backend.service.AsientoContableService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AsientoContableServiceImpl implements AsientoContableService {

    private final AsientoContableRepository asientoRepository;
    private final CuentaContableRepository cuentaRepository;
    private final AsientoContableMapper asientoMapper;
    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;

    private static final Integer CUENTA_CAJA_BANCO = 1;
    private static final Integer CUENTA_CLIENTES = 2;
    private static final Integer CUENTA_VENTAS = 3;
    private static final Integer CUENTA_COSTO_SERVICIO = 4;
    private static final Integer CUENTA_PROVEEDORES = 5;
    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final DetalleReciboRepository detalleReciboRepository;

    @Override
    public List<AsientoContableResponseDTO> listar() {
        return asientoRepository.findAll()
                .stream()
                .map(asientoMapper::toResponse)
                .toList();
    }

    @Override
    public AsientoContableResponseDTO obtenerPorId(Integer id) {
        AsientoContable asiento = asientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento contable no encontrado con ID: " + id));

        return asientoMapper.toResponse(asiento);
    }

    @Override
    @Transactional
    public AsientoContableResponseDTO crear(AsientoContableRequestDTO request) {
        AsientoContable asiento = asientoMapper.toEntity(request);

        List<DetalleAsientoContable> detalles = new ArrayList<>();

        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (DetalleAsientoContableRequestDTO detalleRequest : request.getDetalles()) {

            CuentaContable cuenta = cuentaRepository.findById(detalleRequest.getCuentaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cuenta contable no encontrada con ID: " + detalleRequest.getCuentaId()
                    ));

            BigDecimal debe = detalleRequest.getDebe() != null ? detalleRequest.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = detalleRequest.getHaber() != null ? detalleRequest.getHaber() : BigDecimal.ZERO;

            DetalleAsientoContable detalle = new DetalleAsientoContable();
            detalle.setAsiento(asiento);
            detalle.setCuenta(cuenta);
            detalle.setDebe(debe);
            detalle.setHaber(haber);

            totalDebe = totalDebe.add(debe);
            totalHaber = totalHaber.add(haber);

            detalles.add(detalle);
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new IllegalArgumentException("El total del debe debe ser igual al total del haber");
        }

        asiento.setTotalDebe(totalDebe);
        asiento.setTotalHaber(totalHaber);
        asiento.setDetalles(detalles);

        AsientoContable guardado = asientoRepository.save(asiento);

        return asientoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void anular(Integer id) {
        AsientoContable asiento = asientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento contable no encontrado con ID: " + id));

        asiento.setAnulado(true);
        asientoRepository.save(asiento);
    }

    @Override
    public List<AsientoContableResponseDTO> listarPorOrigen(String origen, Integer origenId) {
        return asientoRepository.findByOrigenAndOrigenId(origen, origenId)
                .stream()
                .map(asientoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void generarAsientoPorPagoPax(PagoPax pagoPax) {
        if (pagoPax == null || pagoPax.getMonto() == null) {
            return;
        }

        crearAsientoAutomatico(
                "Cobro de cliente - Pago PAX",
                "PAGO_PAX",
                pagoPax.getId(),
                pagoPax.getMoneda(),
                pagoPax.getMonto(),
                CUENTA_CAJA_BANCO,
                CUENTA_CLIENTES
        );
    }

@Override
@Transactional
public void generarAsientoPorDocumentoCobranza(DocumentoCobranza documento) {
    if (documento == null || documento.getId() == null) {
        return;
    }

    BigDecimal totalVenta = calcularTotalDocumentoCobranza(documento.getId());

    if (totalVenta.compareTo(BigDecimal.ZERO) <= 0) {
        return;
    }

    crearAsientoAutomatico(
            "Venta registrada - Documento de Cobranza",
            "DOCUMENTO_COBRANZA",
            documento.getId().intValue(),
            "PEN",
            totalVenta,
            CUENTA_CLIENTES,
            CUENTA_VENTAS
    );
}

    @Override
@Transactional
public void generarAsientoPorLiquidacion(Liquidacion liquidacion) {
    if (liquidacion == null || liquidacion.getId() == null) {
        return;
    }

    BigDecimal totalCosto = calcularTotalCostoLiquidacion(liquidacion.getId());

    if (totalCosto.compareTo(BigDecimal.ZERO) <= 0) {
        return;
    }

    crearAsientoAutomatico(
            "Costo de servicio - Liquidación",
            "LIQUIDACION",
            liquidacion.getId(),
            "PEN",
            totalCosto,
            CUENTA_COSTO_SERVICIO,
            CUENTA_PROVEEDORES
    );
}

    @Override
    @Transactional
    public void generarAsientoPorRecibo(Recibo recibo) {
        if (recibo == null || recibo.getId() == null) {
            return;
        }

        List<DetalleRecibo> detalles = detalleReciboRepository.findByReciboId(recibo.getId());

        BigDecimal totalCobrado = detalles.stream()
                .map(d -> {
                    BigDecimal cantidad = d.getCantidad() != null
                            ? BigDecimal.valueOf(d.getCantidad())
                            : BigDecimal.ZERO;
                    BigDecimal precio = d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO;
                    return cantidad.multiply(precio);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCobrado.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        String moneda = recibo.getMoneda() != null ? recibo.getMoneda() : "PEN";

        crearAsientoAutomatico(
                "Cobro de cliente - Recibo " + recibo.getSerie() + "-" + recibo.getCorrelativo(),
                "RECIBO",
                recibo.getId(),
                moneda,
                totalCobrado,
                CUENTA_CAJA_BANCO,
                CUENTA_CLIENTES
        );
    }

    private void crearAsientoAutomatico(
            String glosa,
            String origen,
            Integer origenId,
            String moneda,
            BigDecimal monto,
            Integer cuentaDebeId,
            Integer cuentaHaberId
    ) {
        CuentaContable cuentaDebe = cuentaRepository.findById(cuentaDebeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta debe no encontrada con ID: " + cuentaDebeId));

        CuentaContable cuentaHaber = cuentaRepository.findById(cuentaHaberId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta haber no encontrada con ID: " + cuentaHaberId));

        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(LocalDate.now());
        asiento.setGlosa(glosa);
        asiento.setOrigen(origen);
        asiento.setOrigenId(origenId);
        asiento.setMoneda(moneda);
        asiento.setTotalDebe(monto);
        asiento.setTotalHaber(monto);
        asiento.setAnulado(false);
        asiento.setGeneradoAutomaticamente(true);

        DetalleAsientoContable detalleDebe = new DetalleAsientoContable();
        detalleDebe.setAsiento(asiento);
        detalleDebe.setCuenta(cuentaDebe);
        detalleDebe.setDebe(monto);
        detalleDebe.setHaber(BigDecimal.ZERO);

        DetalleAsientoContable detalleHaber = new DetalleAsientoContable();
        detalleHaber.setAsiento(asiento);
        detalleHaber.setCuenta(cuentaHaber);
        detalleHaber.setDebe(BigDecimal.ZERO);
        detalleHaber.setHaber(monto);

        List<DetalleAsientoContable> detalles = new ArrayList<>();
        detalles.add(detalleDebe);
        detalles.add(detalleHaber);

        asiento.setDetalles(detalles);

        asientoRepository.save(asiento);
    }

    private BigDecimal calcularTotalDocumentoCobranza(Long documentoId) {
    List<DetalleDocumentoCobranza> detalles =
            detalleDocumentoCobranzaRepository.findByDocumentoCobranzaId(documentoId);

    if (detalles == null || detalles.isEmpty()) {
        return BigDecimal.ZERO;
    }

    return detalles.stream()
            .map(this::calcularSubtotalDetalleDocumento)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

    private BigDecimal calcularSubtotalDetalleDocumento(DetalleDocumentoCobranza detalle) {
        BigDecimal cantidad = detalle.getCantidad() != null
                ? BigDecimal.valueOf(detalle.getCantidad())
                : BigDecimal.ZERO;

        BigDecimal precio = detalle.getPrecio() != null
                ? detalle.getPrecio()
                : BigDecimal.ZERO;

        return cantidad.multiply(precio);
    }

    private BigDecimal calcularTotalCostoLiquidacion(Integer liquidacionId) {
    List<DetalleLiquidacion> detalles =
            detalleLiquidacionRepository.findByLiquidacionId(liquidacionId);

    if (detalles == null || detalles.isEmpty()) {
        return BigDecimal.ZERO;
    }

    return detalles.stream()
            .map(this::calcularCostoDetalleLiquidacion)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private BigDecimal calcularCostoDetalleLiquidacion(DetalleLiquidacion detalle) {
    BigDecimal costoTicket = detalle.getCostoTicket() != null
            ? detalle.getCostoTicket()
            : BigDecimal.ZERO;

    BigDecimal cargoServicio = detalle.getCargoServicio() != null
            ? detalle.getCargoServicio()
            : BigDecimal.ZERO;

    return costoTicket.add(cargoServicio);
}
}