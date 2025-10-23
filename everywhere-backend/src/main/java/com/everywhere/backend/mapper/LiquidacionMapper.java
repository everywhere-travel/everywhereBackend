package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.FormaPago;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LiquidacionMapper {

    private final ModelMapper modelMapper;

    public LiquidacionResponseDTO toResponseDTO(Liquidacion liquidacion) {
        LiquidacionResponseDTO dto = modelMapper.map(liquidacion, LiquidacionResponseDTO.class);

        // Mapear relaciones manualmente para asegurar que se carguen correctamente
        if (liquidacion.getProducto() != null) {
            dto.setProducto(modelMapper.map(liquidacion.getProducto(), com.everywhere.backend.model.dto.ProductoResponseDTO.class));
        }

        if (liquidacion.getFormaPago() != null) {
            dto.setFormaPago(modelMapper.map(liquidacion.getFormaPago(), com.everywhere.backend.model.dto.FormaPagoResponseDTO.class));
        }

        return dto;
    }

    public Liquidacion toEntity(LiquidacionRequestDTO dto) {
        Liquidacion liquidacion = new Liquidacion();

        // Mapear campos básicos manualmente
        liquidacion.setNumero(dto.getNumero());
        liquidacion.setFechaCompra(dto.getFechaCompra());
        liquidacion.setDestino(dto.getDestino());
        liquidacion.setNumeroPasajeros(dto.getNumeroPasajeros());

        // Cotización (siempre debe estar presente)
        if (dto.getCotizacionId() != null) {
            Cotizacion cotizacion = new Cotizacion();
            cotizacion.setId(dto.getCotizacionId());
            liquidacion.setCotizacion(cotizacion);
        } else {
            throw new IllegalArgumentException("El ID de cotización es obligatorio para crear una liquidación");
        }

        // Campos opcionales
        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            liquidacion.setProducto(producto);
        }

        if (dto.getFormaPagoId() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(dto.getFormaPagoId());
            liquidacion.setFormaPago(formaPago);
        }

        // OPCIONAL: Carpeta (puede ser null)
        if (dto.getCarpetaId() != null) {
            Carpeta carpeta = new Carpeta();
            carpeta.setId(dto.getCarpetaId());
            liquidacion.setCarpeta(carpeta);
        }

        return liquidacion;
    }

    public void updateEntityFromDTO(LiquidacionRequestDTO dto, Liquidacion entity) {
        entity.setNumero(dto.getNumero());
        entity.setFechaCompra(dto.getFechaCompra());
        entity.setDestino(dto.getDestino());
        entity.setNumeroPasajeros(dto.getNumeroPasajeros());
        entity.setActualizado(LocalDateTime.now());

        if (dto.getCotizacionId() != null) {
            Cotizacion cotizacion = new Cotizacion();
            cotizacion.setId(dto.getCotizacionId());
            entity.setCotizacion(cotizacion);
        } 

        // Actualizar campos opcionales
        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            entity.setProducto(producto);
        } else {
            entity.setProducto(null);
        }

        if (dto.getFormaPagoId() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(dto.getFormaPagoId());
            entity.setFormaPago(formaPago);
        } else {
            entity.setFormaPago(null);
        }

        if (dto.getCarpetaId() != null) {
            Carpeta carpeta = new Carpeta();
            carpeta.setId(dto.getCarpetaId());
            entity.setCarpeta(carpeta);
        } else {
            entity.setCarpeta(null);
        }
    }
}
