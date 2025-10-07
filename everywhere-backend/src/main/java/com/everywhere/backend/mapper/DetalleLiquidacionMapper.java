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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DetalleLiquidacionMapper {

    private final ModelMapper modelMapper;

    public DetalleLiquidacionResponseDTO toResponseDTO(DetalleLiquidacion detalleLiquidacion) {
        DetalleLiquidacionResponseDTO dto = modelMapper.map(detalleLiquidacion, DetalleLiquidacionResponseDTO.class);

        // Mapear relaciones de forma más limpia
        Optional.ofNullable(detalleLiquidacion.getLiquidacion())
                .ifPresent(liquidacion -> dto.setLiquidacion(modelMapper.map(liquidacion, com.everywhere.backend.model.dto.LiquidacionResponseDTO.class)));

        Optional.ofNullable(detalleLiquidacion.getViajero())
                .ifPresent(viajero -> dto.setViajero(modelMapper.map(viajero, com.everywhere.backend.model.dto.ViajeroResponseDTO.class)));

        Optional.ofNullable(detalleLiquidacion.getProducto())
                .ifPresent(producto -> dto.setProducto(modelMapper.map(producto, com.everywhere.backend.model.dto.ProductoResponse.class)));

        Optional.ofNullable(detalleLiquidacion.getProveedor())
                .ifPresent(proveedor -> dto.setProveedor(modelMapper.map(proveedor, com.everywhere.backend.model.dto.ProveedorResponseDTO.class)));

        Optional.ofNullable(detalleLiquidacion.getOperador())
                .ifPresent(operador -> dto.setOperador(modelMapper.map(operador, com.everywhere.backend.model.dto.OperadorResponseDTO.class)));

        return dto;
    }

    public DetalleLiquidacion toEntity(DetalleLiquidacionRequestDTO dto) {
        DetalleLiquidacion entity = new DetalleLiquidacion();
        
        // Mapear campos básicos manualmente para evitar conflictos con ModelMapper
        entity.setTicket(dto.getTicket());
        entity.setCostoTicket(dto.getCostoTicket());
        entity.setCargoServicio(dto.getCargoServicio());
        entity.setValorVenta(dto.getValorVenta());
        entity.setFacturaCompra(dto.getFacturaCompra());
        entity.setBoletaPasajero(dto.getBoletaPasajero());
        entity.setMontoDescuento(dto.getMontoDescuento());
        entity.setPagoPaxUSD(dto.getPagoPaxUSD());
        entity.setPagoPaxPEN(dto.getPagoPaxPEN());
        
        // Establecer relaciones
        setEntityRelations(dto, entity);
        return entity;
    }

    public void updateEntityFromDTO(DetalleLiquidacionRequestDTO dto, DetalleLiquidacion entity) {
        // Mapear campos básicos manualmente para evitar conflictos con ModelMapper
        entity.setTicket(dto.getTicket());
        entity.setCostoTicket(dto.getCostoTicket());
        entity.setCargoServicio(dto.getCargoServicio());
        entity.setValorVenta(dto.getValorVenta());
        entity.setFacturaCompra(dto.getFacturaCompra());
        entity.setBoletaPasajero(dto.getBoletaPasajero());
        entity.setMontoDescuento(dto.getMontoDescuento());
        entity.setPagoPaxUSD(dto.getPagoPaxUSD());
        entity.setPagoPaxPEN(dto.getPagoPaxPEN());
        
        // Actualizar relaciones
        updateEntityRelations(dto, entity); 
        entity.setActualizado(LocalDateTime.now());
    }

    private void setEntityRelations(DetalleLiquidacionRequestDTO dto, DetalleLiquidacion entity) {
        // Para creación - siempre establecer relaciones si se proporcionan
        Optional.ofNullable(dto.getLiquidacionId())
                .ifPresent(id -> {
                    Liquidacion liquidacion = new Liquidacion();
                    liquidacion.setId(id);
                    entity.setLiquidacion(liquidacion);
                });

        Optional.ofNullable(dto.getViajeroId())
                .ifPresent(id -> {
                    Viajero viajero = new Viajero();
                    viajero.setId(id);
                    entity.setViajero(viajero);
                });

        Optional.ofNullable(dto.getProductoId())
                .ifPresent(id -> {
                    Producto producto = new Producto();
                    producto.setId(id);
                    entity.setProducto(producto);
                });

        Optional.ofNullable(dto.getProveedorId())
                .ifPresent(id -> {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setId(id);
                    entity.setProveedor(proveedor);
                });

        Optional.ofNullable(dto.getOperadorId())
                .ifPresent(id -> {
                    Operador operador = new Operador();
                    operador.setId(id);
                    entity.setOperador(operador);
                });
    }

    private void updateEntityRelations(DetalleLiquidacionRequestDTO dto, DetalleLiquidacion entity) {
        // Para actualización - solo cambiar relaciones si se envían explícitamente
        Optional.ofNullable(dto.getLiquidacionId())
                .ifPresent(id -> {
                    Liquidacion liquidacion = new Liquidacion();
                    liquidacion.setId(id);
                    entity.setLiquidacion(liquidacion);
                });

        Optional.ofNullable(dto.getViajeroId())
                .ifPresent(id -> {
                    Viajero viajero = new Viajero();
                    viajero.setId(id);
                    entity.setViajero(viajero);
                });

        Optional.ofNullable(dto.getProductoId())
                .ifPresent(id -> {
                    Producto producto = new Producto();
                    producto.setId(id);
                    entity.setProducto(producto);
                });

        Optional.ofNullable(dto.getProveedorId())
                .ifPresent(id -> {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setId(id);
                    entity.setProveedor(proveedor);
                });

        Optional.ofNullable(dto.getOperadorId())
                .ifPresent(id -> {
                    Operador operador = new Operador();
                    operador.setId(id);
                    entity.setOperador(operador);
                });
    }
}
