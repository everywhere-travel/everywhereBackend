package com.everywhere.backend.mapper;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.Recibo;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;

import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DetalleReciboRepository;
import com.everywhere.backend.repository.ReciboRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReciboMapper {
    
    private final ModelMapper modelMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final DetalleReciboMapper detalleReciboMapper;
    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final ReciboRepository reciboRepository;
    private final DetalleReciboRepository detalleReciboRepository;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(ReciboUpdateDTO.class, Recibo.class).addMappings(mapper -> {
            mapper.skip(Recibo::setDetalleDocumento);
            mapper.skip(Recibo::setSucursal);
            mapper.skip(Recibo::setPersonaJuridica);
        });
    }

    // Mapea desde cotización a recibo
    public Recibo fromCotizacion(CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO,
            String serie, Integer correlativo) {
        Recibo recibo = new Recibo();

        recibo.setSerie(serie);
        recibo.setCorrelativo(correlativo);
        recibo.setMoneda(cotizacionConDetallesResponseDTO.getMoneda());

        Cotizacion cotizacionEntity = new Cotizacion();
        cotizacionEntity.setId(cotizacionConDetallesResponseDTO.getId());
        recibo.setCotizacion(cotizacionEntity);

        if (cotizacionConDetallesResponseDTO.getPersonas() != null) {
            Personas persona = new Personas();
            persona.setId(cotizacionConDetallesResponseDTO.getPersonas().getId());
            recibo.setPersona(persona);
        }

        if (cotizacionConDetallesResponseDTO.getSucursal() != null) {
            Sucursal sucursal = new Sucursal();
            sucursal.setId(cotizacionConDetallesResponseDTO.getSucursal().getId());
            recibo.setSucursal(sucursal);
        }

        if (cotizacionConDetallesResponseDTO.getFormaPago() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(cotizacionConDetallesResponseDTO.getFormaPago().getId());
            recibo.setFormaPago(formaPago);
        }
        return recibo;
    }

    public void updateEntityFromUpdateDTO(Recibo recibo, ReciboUpdateDTO reciboUpdateDTO) {
        modelMapper.map(reciboUpdateDTO, recibo);
    }

    public ReciboResponseDTO toResponseDTO(Recibo recibo) {
        return toResponseDTO(recibo, null, null);
    }

    public ReciboResponseDTO toResponseDTO(Recibo recibo,
            Map<Integer, PersonaNatural> naturalesMap,
            Map<Integer, PersonaJuridica> juridicasMap) {
        return toResponseDTO(recibo, naturalesMap, juridicasMap, null, null);
    }

    public ReciboResponseDTO toResponseDTO(Recibo recibo,
            Map<Integer, PersonaNatural> naturalesMap,
            Map<Integer, PersonaJuridica> juridicasMap,
            Map<Long, java.math.BigDecimal> totalDeudaMap,
            Map<Long, java.math.BigDecimal> totalPagadoMap) {
        ReciboResponseDTO reciboResponseDTO = modelMapper.map(recibo, ReciboResponseDTO.class);

        // Mapear DocumentoCobranza (padre del recibo)
        if (recibo.getDocumentoCobranza() != null) {
            reciboResponseDTO.setDocumentoCobranzaId(recibo.getDocumentoCobranza().getId());
            String numero = recibo.getDocumentoCobranza().getSerie() + "-" +
                    recibo.getDocumentoCobranza().getCorrelativo();
            reciboResponseDTO.setDocumentoCobranzaNumero(numero);
        }

        // Mapear código de cotización
        if (recibo.getCotizacion() != null) {
            reciboResponseDTO.setCotizacionId(recibo.getCotizacion().getId());
            if (recibo.getCotizacion().getCodigoCotizacion() != null) {
                reciboResponseDTO.setCodigoCotizacion(recibo.getCotizacion().getCodigoCotizacion());
            }
        }

        if (recibo.getDetalleDocumento() != null) {
            reciboResponseDTO.setDetalleDocumentoId(recibo.getDetalleDocumento().getId());
            reciboResponseDTO.setClienteDocumento(recibo.getDetalleDocumento().getNumero());
            if (recibo.getDetalleDocumento().getDocumento() != null)
                reciboResponseDTO.setTipoDocumentoCliente(recibo.getDetalleDocumento().getDocumento().getTipo());
        }

        // Siempre setear personaId si existe
        if (recibo.getPersona() != null) {
            reciboResponseDTO.setPersonaId(recibo.getPersona().getId());
        }

        // PRIORIDAD 1: Si hay PersonaJuridica seleccionada, usar sus datos
        if (recibo.getPersonaJuridica() != null) {
            PersonaJuridica pj = recibo.getPersonaJuridica();
            reciboResponseDTO.setPersonaJuridicaId(pj.getId());
            reciboResponseDTO.setPersonaJuridicaRuc(pj.getRuc());
            reciboResponseDTO.setPersonaJuridicaRazonSocial(pj.getRazonSocial());

            // Usar datos de PersonaJuridica para el cliente
            reciboResponseDTO.setClienteNombre(pj.getRazonSocial());
            reciboResponseDTO.setClienteDocumento(pj.getRuc());
            reciboResponseDTO.setTipoDocumentoCliente("RUC");
        }
        // PRIORIDAD 2: Si no hay PersonaJuridica, usar datos de Persona
        else if (recibo.getPersona() != null) {
            Integer personaId = recibo.getPersona().getId();

            PersonaNatural personaNatural = null;
            if (naturalesMap != null) {
                personaNatural = naturalesMap.get(personaId);
            } else {
                personaNatural = personaNaturalRepository.findByPersonasId(personaId).orElse(null);
            }
            if (personaNatural != null) {
                String nombreCompleto = String.join(" ",
                        personaNatural.getNombres() != null ? personaNatural.getNombres().trim() : "",
                        personaNatural.getApellidosPaterno() != null ? personaNatural.getApellidosPaterno().trim() : "",
                        personaNatural.getApellidosMaterno() != null ? personaNatural.getApellidosMaterno().trim() : "")
                        .trim();
                reciboResponseDTO.setClienteNombre(nombreCompleto.isEmpty() ? "Sin nombre" : nombreCompleto);

                if (recibo.getDetalleDocumento() == null) {
                    reciboResponseDTO.setClienteDocumento(personaNatural.getDocumento());
                    reciboResponseDTO.setTipoDocumentoCliente("DNI");
                }
            } else {
                PersonaJuridica personaJuridica = null;
                if (juridicasMap != null) {
                    personaJuridica = juridicasMap.get(personaId);
                } else {
                    personaJuridica = personaJuridicaRepository.findByPersonasId(personaId).orElse(null);
                }
                if (personaJuridica != null) {
                    reciboResponseDTO.setPersonaJuridicaId(personaJuridica.getId());
                    reciboResponseDTO.setPersonaJuridicaRuc(personaJuridica.getRuc());
                    reciboResponseDTO.setPersonaJuridicaRazonSocial(personaJuridica.getRazonSocial());

                    reciboResponseDTO.setClienteNombre(personaJuridica.getRazonSocial());
                    reciboResponseDTO.setClienteDocumento(personaJuridica.getRuc());
                    reciboResponseDTO.setTipoDocumentoCliente("RUC");
                }
            }
        }

        if (recibo.getSucursal() != null) {
            reciboResponseDTO.setSucursalId(recibo.getSucursal().getId());
            reciboResponseDTO.setSucursalDescripcion(recibo.getSucursal().getDescripcion());
        }
        if (recibo.getFormaPago() != null) {
            reciboResponseDTO.setFormaPagoId(recibo.getFormaPago().getId());
            reciboResponseDTO.setFormaPagoDescripcion(recibo.getFormaPago().getDescripcion());
        }

        // Mapear los detalles
        if (recibo.getDetalleRecibo() != null && !recibo.getDetalleRecibo().isEmpty()) {
            List<DetalleReciboResponseDTO> detallesDTO = recibo.getDetalleRecibo().stream()
                    .map(detalleReciboMapper::toResponseDTO).toList();
            reciboResponseDTO.setDetalles(detallesDTO);
        }

        calcularEstadoDeCuenta(recibo, reciboResponseDTO, totalDeudaMap, totalPagadoMap);

        return reciboResponseDTO;
    }

    private void calcularEstadoDeCuenta(Recibo recibo, ReciboResponseDTO dto,
            Map<Long, java.math.BigDecimal> totalDeudaMap,
            Map<Long, java.math.BigDecimal> totalPagadoMap) {
        if (recibo.getDocumentoCobranza() == null || recibo.getDocumentoCobranza().getId() == null) {
            return;
        }

        Long documentoId = recibo.getDocumentoCobranza().getId();

        // 1. Total Deuda Original: usar mapa pre-calculado si está disponible
        java.math.BigDecimal totalDeuda;
        if (totalDeudaMap != null) {
            totalDeuda = totalDeudaMap.getOrDefault(documentoId, java.math.BigDecimal.ZERO);
        } else {
            // fallback para llamadas individuales
            List<DetalleDocumentoCobranza> detallesDoc = detalleDocumentoCobranzaRepository
                    .findByDocumentoCobranzaId(documentoId);
            totalDeuda = java.math.BigDecimal.ZERO;
            if (detallesDoc != null) {
                for (DetalleDocumentoCobranza detalle : detallesDoc) {
                    java.math.BigDecimal cantidad = detalle.getCantidad() != null
                            ? java.math.BigDecimal.valueOf(detalle.getCantidad())
                            : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal precio = detalle.getPrecio() != null ? detalle.getPrecio() : java.math.BigDecimal.ZERO;
                    totalDeuda = totalDeuda.add(cantidad.multiply(precio));
                }
            }
        }

        // 2. Total Pagado Acumulado: usar mapa pre-calculado si está disponible
        java.math.BigDecimal totalPagadoAcumulado;
        if (totalPagadoMap != null) {
            totalPagadoAcumulado = totalPagadoMap.getOrDefault(documentoId, java.math.BigDecimal.ZERO);
        } else {
            // fallback: sumar todos los recibos del documento
            List<Recibo> recibosHermanos = reciboRepository.findByDocumentoCobranzaId(documentoId);
            totalPagadoAcumulado = java.math.BigDecimal.ZERO;
            if (recibosHermanos != null) {
                for (Recibo r : recibosHermanos) {
                    List<DetalleRecibo> detallesR = detalleReciboRepository.findByReciboId(r.getId());
                    if (detallesR != null) {
                        for (DetalleRecibo dr : detallesR) {
                            java.math.BigDecimal c = dr.getCantidad() != null ? java.math.BigDecimal.valueOf(dr.getCantidad()) : java.math.BigDecimal.ZERO;
                            java.math.BigDecimal p = dr.getPrecio() != null ? dr.getPrecio() : java.math.BigDecimal.ZERO;
                            totalPagadoAcumulado = totalPagadoAcumulado.add(c.multiply(p));
                        }
                    }
                }
            }
        }

        // 3. Saldo Pendiente Actual
        java.math.BigDecimal saldoPendiente = totalDeuda.subtract(totalPagadoAcumulado);

        dto.setTotalDeudaDocumento(totalDeuda);
        dto.setTotalPagadoAcumulado(totalPagadoAcumulado);
        dto.setSaldoPendienteActual(saldoPendiente);
    }
}
