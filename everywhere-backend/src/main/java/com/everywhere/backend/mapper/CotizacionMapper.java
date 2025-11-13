package com.everywhere.backend.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.Cotizacion;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CotizacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(CotizacionRequestDto.class, Cotizacion.class).addMappings(mapper -> {
            mapper.skip(Cotizacion::setCounter);
            mapper.skip(Cotizacion::setFormaPago);
            mapper.skip(Cotizacion::setEstadoCotizacion);
            mapper.skip(Cotizacion::setSucursal);
            mapper.skip(Cotizacion::setCarpeta);
        });
    }

    public CotizacionResponseDto toResponse(Cotizacion cotizacion) { 
        return modelMapper.map(cotizacion, CotizacionResponseDto.class);
    }

    public Cotizacion toEntity(CotizacionRequestDto cotizacionRequestDto) { 
        return modelMapper.map(cotizacionRequestDto, Cotizacion.class);
    }
    
    public void updateEntityFromRequest(Cotizacion cotizacion, CotizacionRequestDto cotizacionRequestDto) {
        modelMapper.map(cotizacionRequestDto, cotizacion);
    }

    public CotizacionConDetallesResponseDTO toResponseWithDetalles(CotizacionResponseDto cotizacionResponseDto, 
        List<DetalleCotizacionSimpleDTO> detalleCotizacionSimpleDTOs) {
        CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO = modelMapper.map(cotizacionResponseDto, CotizacionConDetallesResponseDTO.class);
        cotizacionConDetallesResponseDTO.setDetalles(detalleCotizacionSimpleDTOs);
        return cotizacionConDetallesResponseDTO;
    }

    public DetalleCotizacionSimpleDTO toDetalleSimple(DetalleCotizacionResponseDto detalleCotizacionResponseDto) {
        return modelMapper.map(detalleCotizacionResponseDto, DetalleCotizacionSimpleDTO.class);
    }
}