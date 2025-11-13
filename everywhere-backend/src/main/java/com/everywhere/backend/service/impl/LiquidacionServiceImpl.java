package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSimpleDTO;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
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
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        return liquidacionRepository.findAll().stream().map(liquidacionMapper::toResponseDTO).toList();
    }

    @Override
    public LiquidacionResponseDTO findById(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));
        return liquidacionMapper.toResponseDTO(liquidacion);
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO) {
        if (!liquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);

        if (liquidacionRequestDTO.getCotizacionId() != null && 
            !cotizacionRepository.existsById(liquidacionRequestDTO.getCotizacionId()))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + liquidacionRequestDTO.getCotizacionId());
        
        if (liquidacionRequestDTO.getProductoId() != null && 
            !productoRepository.existsById(liquidacionRequestDTO.getProductoId()))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + liquidacionRequestDTO.getProductoId());

        if (liquidacionRequestDTO.getFormaPagoId() != null && 
            !formaPagoRepository.existsById(liquidacionRequestDTO.getFormaPagoId()))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + liquidacionRequestDTO.getFormaPagoId());

        if (liquidacionRequestDTO.getCarpetaId() != null && 
            !carpetaRepository.existsById(liquidacionRequestDTO.getCarpetaId()))
            throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + liquidacionRequestDTO.getCarpetaId());

        Liquidacion liquidacion = liquidacionRepository.findById(id).get();
        liquidacionMapper.updateEntityFromRequest(liquidacion, liquidacionRequestDTO);

        if (liquidacionRequestDTO.getCotizacionId() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(liquidacionRequestDTO.getCotizacionId()).get();
            liquidacion.setCotizacion(cotizacion);
        }
        
        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId()).get();
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId()).get();
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId()).get();
            liquidacion.setCarpeta(carpeta);
        }

        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!liquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);
        liquidacionRepository.deleteById(id);
    }

    @Override
    public LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        LiquidacionResponseDTO liquidacionResponseDTO = liquidacionMapper.toResponseDTO(liquidacion);

        List<DetalleLiquidacionResponseDTO> detalleLiquidacionResponseDTOs = detalleLiquidacionService.findByLiquidacionId(id); // Obtener los detalles simplificados (sin liquidación repetida)
        List<DetalleLiquidacionSimpleDTO> detalleLiquidacionSimpleDTOs = detalleLiquidacionResponseDTOs.stream() // Convertir a detalles simples (sin liquidación)
                .map(this::convertirADetalleSimple).toList();

        // Obtener las observaciones simplificadas (sin liquidación repetida)
        List<ObservacionLiquidacionResponseDTO> observacionLiquidacionResponseDTOS = observacionLiquidacionService.findByLiquidacionId(id);
        // Convertir a observaciones simples (sin liquidación)
        List<ObservacionLiquidacionSimpleDTO> observacionLiquidacionSimpleDTOs = 
                observacionLiquidacionResponseDTOS.stream().map(this::convertirAObservacionSimple).toList();

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

    private DetalleLiquidacionSimpleDTO convertirADetalleSimple(DetalleLiquidacionResponseDTO detalleLiquidacionResponseDTO) {
        DetalleLiquidacionSimpleDTO detalleLiquidacionSimpleDTO = new DetalleLiquidacionSimpleDTO();
        detalleLiquidacionSimpleDTO.setId(detalleLiquidacionResponseDTO.getId());
        detalleLiquidacionSimpleDTO.setTicket(detalleLiquidacionResponseDTO.getTicket());
        detalleLiquidacionSimpleDTO.setCostoTicket(detalleLiquidacionResponseDTO.getCostoTicket());
        detalleLiquidacionSimpleDTO.setCargoServicio(detalleLiquidacionResponseDTO.getCargoServicio());
        detalleLiquidacionSimpleDTO.setValorVenta(detalleLiquidacionResponseDTO.getValorVenta());
        detalleLiquidacionSimpleDTO.setFacturaCompra(detalleLiquidacionResponseDTO.getFacturaCompra());
        detalleLiquidacionSimpleDTO.setBoletaPasajero(detalleLiquidacionResponseDTO.getBoletaPasajero());
        detalleLiquidacionSimpleDTO.setMontoDescuento(detalleLiquidacionResponseDTO.getMontoDescuento());
        detalleLiquidacionSimpleDTO.setPagoPaxUSD(detalleLiquidacionResponseDTO.getPagoPaxUSD());
        detalleLiquidacionSimpleDTO.setPagoPaxPEN(detalleLiquidacionResponseDTO.getPagoPaxPEN());
        detalleLiquidacionSimpleDTO.setCreado(detalleLiquidacionResponseDTO.getCreado());
        detalleLiquidacionSimpleDTO.setActualizado(detalleLiquidacionResponseDTO.getActualizado());

        // Mapear todas las relaciones (excepto liquidación)
        detalleLiquidacionSimpleDTO.setViajero(detalleLiquidacionResponseDTO.getViajero());
        detalleLiquidacionSimpleDTO.setProducto(detalleLiquidacionResponseDTO.getProducto());
        detalleLiquidacionSimpleDTO.setProveedor(detalleLiquidacionResponseDTO.getProveedor());
        detalleLiquidacionSimpleDTO.setOperador(detalleLiquidacionResponseDTO.getOperador());

        return detalleLiquidacionSimpleDTO;
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO create(LiquidacionRequestDTO liquidacionRequestDTO, Integer cotizacionId) {
        if (!cotizacionRepository.existsById(cotizacionId))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);

        if (liquidacionRequestDTO.getProductoId() != null && 
            !productoRepository.existsById(liquidacionRequestDTO.getProductoId()))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + liquidacionRequestDTO.getProductoId());

        if (liquidacionRequestDTO.getFormaPagoId() != null && 
            !formaPagoRepository.existsById(liquidacionRequestDTO.getFormaPagoId()))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + liquidacionRequestDTO.getFormaPagoId());

        if (liquidacionRequestDTO.getCarpetaId() != null && 
            !carpetaRepository.existsById(liquidacionRequestDTO.getCarpetaId()))
            throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + liquidacionRequestDTO.getCarpetaId());

        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId).get();
        
        Liquidacion liquidacion = liquidacionMapper.toEntity(liquidacionRequestDTO);
        liquidacion.setCotizacion(cotizacion);

        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId()).get();
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId()).get();
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId()).get();
            liquidacion.setCarpeta(carpeta);
        }
        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }

    private ObservacionLiquidacionSimpleDTO convertirAObservacionSimple(ObservacionLiquidacionResponseDTO observacionLiquidacionResponseDTO) {
        ObservacionLiquidacionSimpleDTO observacionLiquidacionSimpleDTO = new ObservacionLiquidacionSimpleDTO();
        observacionLiquidacionSimpleDTO.setId(observacionLiquidacionResponseDTO.getId());
        observacionLiquidacionSimpleDTO.setDescripcion(observacionLiquidacionResponseDTO.getDescripcion());
        observacionLiquidacionSimpleDTO.setValor(observacionLiquidacionResponseDTO.getValor());
        observacionLiquidacionSimpleDTO.setDocumento(observacionLiquidacionResponseDTO.getDocumento());
        observacionLiquidacionSimpleDTO.setNumeroDocumento(observacionLiquidacionResponseDTO.getNumeroDocumento());
        observacionLiquidacionSimpleDTO.setCreado(observacionLiquidacionResponseDTO.getCreado());
        observacionLiquidacionSimpleDTO.setActualizado(observacionLiquidacionResponseDTO.getActualizado());
        // NO incluimos la liquidación para evitar referencia circular
        return observacionLiquidacionSimpleDTO;
    }
}