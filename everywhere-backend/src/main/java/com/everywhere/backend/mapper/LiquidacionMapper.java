package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.Carpeta;
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
            dto.setProducto(modelMapper.map(liquidacion.getProducto(), com.everywhere.backend.model.dto.ProductoResponse.class));
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
        liquidacion.setFechaVencimiento(dto.getFechaVencimiento());
        liquidacion.setDestino(dto.getDestino());
        liquidacion.setNumeroPasajeros(dto.getNumeroPasajeros());
        liquidacion.setObservacion(dto.getObservacion());

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

        // Dejar campos de relación como null para evitar restricciones
        liquidacion.setCotizacion(null);
        liquidacion.setCarpeta(null);

        return liquidacion;
    }

    public void updateEntityFromDTO(LiquidacionRequestDTO dto, Liquidacion entity) {
        entity.setNumero(dto.getNumero());
        entity.setFechaCompra(dto.getFechaCompra());
        entity.setFechaVencimiento(dto.getFechaVencimiento());
        entity.setDestino(dto.getDestino());
        entity.setNumeroPasajeros(dto.getNumeroPasajeros());
        entity.setObservacion(dto.getObservacion());
        entity.setActualizado(LocalDateTime.now());

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

        // No tocar cotización ni carpeta para evitar conflictos
    }
}
