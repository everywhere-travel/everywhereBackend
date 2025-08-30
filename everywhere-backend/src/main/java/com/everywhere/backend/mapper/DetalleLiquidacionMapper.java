package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.model.entity.Operador;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DetalleLiquidacionMapper {

    private final ModelMapper modelMapper;

    public DetalleLiquidacionResponseDTO toResponseDTO(DetalleLiquidacion detalleLiquidacion) {
        DetalleLiquidacionResponseDTO dto = modelMapper.map(detalleLiquidacion, DetalleLiquidacionResponseDTO.class);

        // Mapear todas las relaciones que tienen DTOs existentes
        if (detalleLiquidacion.getLiquidacion() != null) {
            dto.setLiquidacion(modelMapper.map(detalleLiquidacion.getLiquidacion(), com.everywhere.backend.model.dto.LiquidacionResponseDTO.class));
        }

        if (detalleLiquidacion.getViajero() != null) {
            dto.setViajero(modelMapper.map(detalleLiquidacion.getViajero(), com.everywhere.backend.model.dto.ViajeroResponseDTO.class));
        }

        if (detalleLiquidacion.getProducto() != null) {
            dto.setProducto(modelMapper.map(detalleLiquidacion.getProducto(), com.everywhere.backend.model.dto.ProductoResponse.class));
        }

        if (detalleLiquidacion.getProveedor() != null) {
            dto.setProveedor(modelMapper.map(detalleLiquidacion.getProveedor(), com.everywhere.backend.model.dto.ProveedorResponseDTO.class));
        }

        if (detalleLiquidacion.getOperador() != null) {
            dto.setOperador(modelMapper.map(detalleLiquidacion.getOperador(), com.everywhere.backend.model.dto.OperadorResponseDTO.class));
        }

        return dto;
    }

    public DetalleLiquidacion toEntity(DetalleLiquidacionRequestDTO dto) {
        DetalleLiquidacion detalleLiquidacion = new DetalleLiquidacion();

        // Mapear campos básicos manualmente
        detalleLiquidacion.setTicket(dto.getTicket());
        detalleLiquidacion.setCostoTicket(dto.getCostoTicket());
        detalleLiquidacion.setCargoServicio(dto.getCargoServicio());
        detalleLiquidacion.setValorVenta(dto.getValorVenta());
        detalleLiquidacion.setFacturaCompra(dto.getFacturaCompra());
        detalleLiquidacion.setBoletaPasajero(dto.getBoletaPasajero());
        detalleLiquidacion.setMontoDescuento(dto.getMontoDescuento());
        detalleLiquidacion.setPagoPaxUSD(dto.getPagoPaxUSD());
        detalleLiquidacion.setPagoPaxPEN(dto.getPagoPaxPEN());

        // Configurar relaciones usando IDs
        if (dto.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(dto.getLiquidacionId());
            detalleLiquidacion.setLiquidacion(liquidacion);
        }

        if (dto.getViajeroId() != null) {
            Viajero viajero = new Viajero();
            viajero.setId(dto.getViajeroId());
            detalleLiquidacion.setViajero(viajero);
        }

        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            detalleLiquidacion.setProducto(producto);
        }

        if (dto.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(dto.getProveedorId());
            detalleLiquidacion.setProveedor(proveedor);
        }

        if (dto.getOperadorId() != null) {
            Operador operador = new Operador();
            operador.setId(dto.getOperadorId());
            detalleLiquidacion.setOperador(operador);
        }

        return detalleLiquidacion;
    }

    public void updateEntityFromDTO(DetalleLiquidacionRequestDTO dto, DetalleLiquidacion entity) {
        entity.setTicket(dto.getTicket());
        entity.setCostoTicket(dto.getCostoTicket());
        entity.setCargoServicio(dto.getCargoServicio());
        entity.setValorVenta(dto.getValorVenta());
        entity.setFacturaCompra(dto.getFacturaCompra());
        entity.setBoletaPasajero(dto.getBoletaPasajero());
        entity.setMontoDescuento(dto.getMontoDescuento());
        entity.setPagoPaxUSD(dto.getPagoPaxUSD());
        entity.setPagoPaxPEN(dto.getPagoPaxPEN());
        entity.setActualizado(LocalDateTime.now());

        // Actualizar relaciones
        if (dto.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(dto.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }

        if (dto.getViajeroId() != null) {
            Viajero viajero = new Viajero();
            viajero.setId(dto.getViajeroId());
            entity.setViajero(viajero);
        }

        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            entity.setProducto(producto);
        }

        if (dto.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(dto.getProveedorId());
            entity.setProveedor(proveedor);
        }

        if (dto.getOperadorId() != null) {
            Operador operador = new Operador();
            operador.setId(dto.getOperadorId());
            entity.setOperador(operador);
        }
    }
}
