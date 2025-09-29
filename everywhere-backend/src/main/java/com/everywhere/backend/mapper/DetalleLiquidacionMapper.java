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
import org.modelmapper.convention.MatchingStrategies;
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
        DetalleLiquidacion entity = modelMapper.map(dto, DetalleLiquidacion.class);

        setEntityRelations(dto, entity);
        return entity;
    }

    public void updateEntityFromDTO(DetalleLiquidacionRequestDTO dto, DetalleLiquidacion entity) {
        // Configurar ModelMapper para ignorar valores null durante el mapeo
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(context -> context.getSource() != null);

        // Mapear solo campos no null usando ModelMapper
        modelMapper.map(dto, entity);

        // SOLO actualizar relaciones si realmente se quiere cambiar la relación
        // No sobrescribir relaciones existentes si no se envían IDs
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
