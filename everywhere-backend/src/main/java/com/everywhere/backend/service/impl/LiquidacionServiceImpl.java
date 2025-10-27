package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSimpleDTO;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionSimpleDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.LiquidacionService;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.service.ObservacionLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.LiquidacionMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements LiquidacionService {

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionMapper liquidacionMapper;
    private final DetalleLiquidacionService detalleLiquidacionService;
    private final CotizacionRepository cotizacionRepository;
    private final CarpetaRepository carpetaRepository;
    private final ObservacionLiquidacionService observacionLiquidacionService;
    private final FormaPagoRepository formaPagoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<LiquidacionResponseDTO> findAll() {
        return liquidacionRepository.findAllWithRelations().stream()
                .map(liquidacionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public LiquidacionResponseDTO findById(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));
        return liquidacionMapper.toResponseDTO(liquidacion);
    }

    @Override
    public LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        liquidacionMapper.updateEntityFromRequest(liquidacion, liquidacionRequestDTO);

        if (liquidacionRequestDTO.getCotizacionId() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(liquidacionRequestDTO.getCotizacionId())
                    .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));
            liquidacion.setCotizacion(cotizacion);
        }
        
        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId())
                    .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId())
                    .orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada"));
            liquidacion.setCarpeta(carpeta);
        }

        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }

    @Override
    public void deleteById(Integer id) {
        if (!liquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);
        liquidacionRepository.deleteById(id);
    }

    @Override
    public LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        LiquidacionResponseDTO liquidacionResponseDTO = liquidacionMapper.toResponseDTO(liquidacion);

        List<DetalleLiquidacionResponseDTO> detalleLiquidacionResponseDTOs = detalleLiquidacionService.findByLiquidacionId(id); // Obtener los detalles simplificados (sin liquidación repetida)
        List<DetalleLiquidacionSimpleDTO> detalleLiquidacionSimpleDTOs = detalleLiquidacionResponseDTOs.stream() // Convertir a detalles simples (sin liquidación)
                .map(this::convertirADetalleSimple).collect(Collectors.toList());

        // Obtener las observaciones simplificadas (sin liquidación repetida)
        List<ObeservacionLiquidacionResponseDTO> obeservacionLiquidacionResponseDTOs = observacionLiquidacionService.findByLiquidacionId(id);
        // Convertir a observaciones simples (sin liquidación)
        List<ObservacionLiquidacionSimpleDTO> observacionLiquidacionSimpleDTOs = obeservacionLiquidacionResponseDTOs.stream()
                .map(this::convertirAObservacionSimple).collect(Collectors.toList());

        // Crear el DTO con detalles y observaciones
        LiquidacionConDetallesResponseDTO liquidacionConDetallesResponseDTO = new LiquidacionConDetallesResponseDTO();
        liquidacionConDetallesResponseDTO.setId(liquidacionResponseDTO.getId());
        liquidacionConDetallesResponseDTO.setNumero(liquidacionResponseDTO.getNumero());
        liquidacionConDetallesResponseDTO.setFechaCompra(liquidacionResponseDTO.getFechaCompra());
        liquidacionConDetallesResponseDTO.setDestino(liquidacionResponseDTO.getDestino());
        liquidacionConDetallesResponseDTO.setNumeroPasajeros(liquidacionResponseDTO.getNumeroPasajeros());
        liquidacionConDetallesResponseDTO.setCreado(liquidacionResponseDTO.getCreado());
        liquidacionConDetallesResponseDTO.setActualizado(liquidacionResponseDTO.getActualizado());
        liquidacionConDetallesResponseDTO.setProducto(liquidacionResponseDTO.getProducto());
        liquidacionConDetallesResponseDTO.setFormaPago(liquidacionResponseDTO.getFormaPago());
        liquidacionConDetallesResponseDTO.setDetalles(detalleLiquidacionSimpleDTOs);
        liquidacionConDetallesResponseDTO.setObservaciones(observacionLiquidacionSimpleDTOs);

        return liquidacionConDetallesResponseDTO;
    }

    private DetalleLiquidacionSimpleDTO convertirADetalleSimple(DetalleLiquidacionResponseDTO detalleCompleto) {
        DetalleLiquidacionSimpleDTO detalleSimple = new DetalleLiquidacionSimpleDTO();
        detalleSimple.setId(detalleCompleto.getId());
        detalleSimple.setTicket(detalleCompleto.getTicket());
        detalleSimple.setCostoTicket(detalleCompleto.getCostoTicket());
        detalleSimple.setCargoServicio(detalleCompleto.getCargoServicio());
        detalleSimple.setValorVenta(detalleCompleto.getValorVenta());
        detalleSimple.setFacturaCompra(detalleCompleto.getFacturaCompra());
        detalleSimple.setBoletaPasajero(detalleCompleto.getBoletaPasajero());
        detalleSimple.setMontoDescuento(detalleCompleto.getMontoDescuento());
        detalleSimple.setPagoPaxUSD(detalleCompleto.getPagoPaxUSD());
        detalleSimple.setPagoPaxPEN(detalleCompleto.getPagoPaxPEN());
        detalleSimple.setCreado(detalleCompleto.getCreado());
        detalleSimple.setActualizado(detalleCompleto.getActualizado());

        // Mapear todas las relaciones (excepto liquidación)
        detalleSimple.setViajero(detalleCompleto.getViajero());
        detalleSimple.setProducto(detalleCompleto.getProducto());
        detalleSimple.setProveedor(detalleCompleto.getProveedor());
        detalleSimple.setOperador(detalleCompleto.getOperador());

        return detalleSimple;
    }

    @Override
    public LiquidacionResponseDTO create(LiquidacionRequestDTO liquidacionRequestDTO, Integer cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

        Liquidacion liquidacion = liquidacionMapper.toEntity(liquidacionRequestDTO);
        liquidacion.setCotizacion(cotizacion);

        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId())
                    .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId())
                    .orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada"));
            liquidacion.setCarpeta(carpeta);
        }

        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }

    private ObservacionLiquidacionSimpleDTO convertirAObservacionSimple(ObeservacionLiquidacionResponseDTO obeservacionLiquidacionResponseDTO) {
        ObservacionLiquidacionSimpleDTO observacionLiquidacionSimpleDTO = new ObservacionLiquidacionSimpleDTO();
        observacionLiquidacionSimpleDTO.setId(obeservacionLiquidacionResponseDTO.getId());
        observacionLiquidacionSimpleDTO.setDescripcion(obeservacionLiquidacionResponseDTO.getDescripcion());
        observacionLiquidacionSimpleDTO.setValor(obeservacionLiquidacionResponseDTO.getValor());
        observacionLiquidacionSimpleDTO.setDocumento(obeservacionLiquidacionResponseDTO.getDocumento());
        observacionLiquidacionSimpleDTO.setNumeroDocumento(obeservacionLiquidacionResponseDTO.getNumeroDocumento());
        observacionLiquidacionSimpleDTO.setCreado(obeservacionLiquidacionResponseDTO.getCreado());
        observacionLiquidacionSimpleDTO.setActualizado(obeservacionLiquidacionResponseDTO.getActualizado());
        // NO incluimos la liquidación para evitar referencia circular
        return observacionLiquidacionSimpleDTO;
    }
}