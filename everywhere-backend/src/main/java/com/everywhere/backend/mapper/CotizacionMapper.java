package com.everywhere.backend.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;

@Component
@RequiredArgsConstructor
public class CotizacionMapper {

    private final ModelMapper modelMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;

    public record ClienteInfo(String nombre, String identificador) {}

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

    public Map<Integer, ClienteInfo> resolveClienteInfo(List<Cotizacion> cotizaciones) {
        List<Integer> personaIds = cotizaciones.stream()
                .map(Cotizacion::getPersonas)
                .filter(Objects::nonNull)
                .map(Personas::getId)
                .distinct()
                .toList();

        if (personaIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, PersonaNatural> naturales = personaNaturalRepository.findByPersonasIdIn(personaIds).stream()
                .collect(Collectors.toMap(pn -> pn.getPersonas().getId(), pn -> pn, (a, b) -> a));
        Map<Integer, PersonaJuridica> juridicas = personaJuridicaRepository.findByPersonasIdIn(personaIds).stream()
                .collect(Collectors.toMap(pj -> pj.getPersonas().getId(), pj -> pj, (a, b) -> a));

        Map<Integer, ClienteInfo> result = new HashMap<>();
        naturales.forEach((personaId, natural) ->
                result.put(personaId, new ClienteInfo(construirNombreCompleto(natural), natural.getDocumento())));
        juridicas.forEach((personaId, juridica) ->
                result.putIfAbsent(personaId, new ClienteInfo(juridica.getRazonSocial(), juridica.getRuc())));
        return result;
    }

    private String construirNombreCompleto(PersonaNatural natural) {
        String nombres = natural.getNombres() != null ? natural.getNombres().trim() : "";
        String paterno = natural.getApellidosPaterno() != null ? natural.getApellidosPaterno().trim() : "";
        String materno = natural.getApellidosMaterno() != null ? natural.getApellidosMaterno().trim() : "";
        String fullName = nombres;
        if (!paterno.isEmpty()) fullName += (fullName.isEmpty() ? "" : " ") + paterno;
        if (!materno.isEmpty()) fullName += (fullName.isEmpty() ? "" : " ") + materno;
        return fullName;
    }


    public CotizacionResponseDto toResponse(Cotizacion cotizacion) {
        Map<Integer, ClienteInfo> clienteInfoMap = cotizacion.getPersonas() != null
                ? resolveClienteInfo(List.of(cotizacion))
                : Map.of();
        return toResponse(cotizacion, clienteInfoMap);
    }

    public CotizacionResponseDto toResponse(Cotizacion cotizacion, Map<Integer, ClienteInfo> clienteInfoMap) {
        CotizacionResponseDto dto = modelMapper.map(cotizacion, CotizacionResponseDto.class);
        if (cotizacion.getPersonas() != null) {
            ClienteInfo info = clienteInfoMap.get(cotizacion.getPersonas().getId());
            if (info != null) {
                dto.setClienteNombre(info.nombre());
                dto.setClienteIdentificador(info.identificador());
            }
        }
        return dto;
    }

    public Cotizacion toEntity(CotizacionRequestDto cotizacionRequestDto) { 
        return modelMapper.map(cotizacionRequestDto, Cotizacion.class);
    }
    
    public void updateEntityFromRequest(Cotizacion cotizacion, CotizacionRequestDto cotizacionRequestDto) {
        if (cotizacionRequestDto.getNombreCotizacion() != null) {
            cotizacion.setNombreCotizacion(cotizacionRequestDto.getNombreCotizacion());
        }
        if (cotizacionRequestDto.getCantAdultos() != null) {
            cotizacion.setCantAdultos(cotizacionRequestDto.getCantAdultos());
        }
        if (cotizacionRequestDto.getCantNinos() != null) {
            cotizacion.setCantNinos(cotizacionRequestDto.getCantNinos());
        }
        if (cotizacionRequestDto.getFechaVencimiento() != null) {
            cotizacion.setFechaVencimiento(cotizacionRequestDto.getFechaVencimiento());
        }
        if (cotizacionRequestDto.getOrigenDestino() != null) {
            cotizacion.setOrigenDestino(cotizacionRequestDto.getOrigenDestino());
        }
        if (cotizacionRequestDto.getFechaSalida() != null) {
            cotizacion.setFechaSalida(cotizacionRequestDto.getFechaSalida());
        }
        if (cotizacionRequestDto.getFechaRegreso() != null) {
            cotizacion.setFechaRegreso(cotizacionRequestDto.getFechaRegreso());
        }
        if (cotizacionRequestDto.getMoneda() != null) {
            cotizacion.setMoneda(cotizacionRequestDto.getMoneda());
        }
        if (cotizacionRequestDto.getObservacion() != null) {
            cotizacion.setObservacion(cotizacionRequestDto.getObservacion());
        }
    }

    public CotizacionConDetallesResponseDTO toResponseWithDetalles(CotizacionResponseDto cotizacionResponseDto,
        List<DetalleCotizacionSimpleDTO> detalleCotizacionSimpleDTOs) {
        CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO = modelMapper.map(cotizacionResponseDto, CotizacionConDetallesResponseDTO.class);
        cotizacionConDetallesResponseDTO.setDetalles(detalleCotizacionSimpleDTOs);
        cotizacionConDetallesResponseDTO.setGrupoSeleccionadoId(resolveGrupoSeleccionado(detalleCotizacionSimpleDTOs));
        return cotizacionConDetallesResponseDTO;
    }

    private Integer resolveGrupoSeleccionado(List<DetalleCotizacionSimpleDTO> detalles) {
        if (detalles == null) {
            return null;
        }
        return detalles.stream()
                .filter(d -> Boolean.TRUE.equals(d.getSeleccionado()))
                .map(DetalleCotizacionSimpleDTO::getCategoria)
                .filter(Objects::nonNull)
                .filter(categoria -> categoria.getId() != 1)
                .map(CategoriaResponseDto::getId)
                .findFirst()
                .orElse(null);
    }

    public DetalleCotizacionSimpleDTO toDetalleSimple(DetalleCotizacionResponseDto detalleCotizacionResponseDto) {
        return modelMapper.map(detalleCotizacionResponseDto, DetalleCotizacionSimpleDTO.class);
    }
}