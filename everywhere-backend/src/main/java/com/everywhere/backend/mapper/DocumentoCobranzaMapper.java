package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO; 
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoCobranzaMapper {

    private final ModelMapper modelMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    
    public DocumentoCobranza toEntity(DocumentoCobranzaRequestDTO documentoCobranzaRequestDTO) {
        return modelMapper.map(documentoCobranzaRequestDTO, DocumentoCobranza.class);
    }

    //maneja la lógica de mapeo desde cotización a documento de cobranza
    public DocumentoCobranza fromCotizacion(CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO, String numeroDocumento) {
        DocumentoCobranza documentoCobranza = new DocumentoCobranza();
        
        documentoCobranza.setNumero(numeroDocumento);
        documentoCobranza.setObservaciones(cotizacionConDetallesResponseDTO.getObservacion());
        documentoCobranza.setMoneda(cotizacionConDetallesResponseDTO.getMoneda());

        Cotizacion cotizacionEntity = new Cotizacion();
        cotizacionEntity.setId(cotizacionConDetallesResponseDTO.getId());
        documentoCobranza.setCotizacion(cotizacionEntity);

        if (cotizacionConDetallesResponseDTO.getPersonas() != null) {
            Personas persona = new Personas();
            persona.setId(cotizacionConDetallesResponseDTO.getPersonas().getId());
            documentoCobranza.setPersona(persona);
        }

        if (cotizacionConDetallesResponseDTO.getSucursal() != null) {
            Sucursal sucursal = new Sucursal();
            sucursal.setId(cotizacionConDetallesResponseDTO.getSucursal().getId());
            documentoCobranza.setSucursal(sucursal);
        }

        if (cotizacionConDetallesResponseDTO.getFormaPago() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(cotizacionConDetallesResponseDTO.getFormaPago().getId());
            documentoCobranza.setFormaPago(formaPago);
        }
        return documentoCobranza;
    }

    public DocumentoCobranzaRequestDTO toRequestDTO(DocumentoCobranza documentoCobranza) {
        return modelMapper.map(documentoCobranza, DocumentoCobranzaRequestDTO.class);
    }

    public void updateEntityFromRequest(DocumentoCobranza documentoCobranza, DocumentoCobranzaRequestDTO documentoCobranzaRequestDTO) {
        modelMapper.map(documentoCobranzaRequestDTO, documentoCobranza);
    }

    public void updateEntityFromUpdateDTO(DocumentoCobranza documentoCobranza, DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        modelMapper.map(documentoCobranzaUpdateDTO, documentoCobranza);
    }

    public DocumentoCobranzaResponseDTO toResponseDTO(DocumentoCobranza documentoCobranza) {
        DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO = modelMapper.map(documentoCobranza, DocumentoCobranzaResponseDTO.class);
        
        if (documentoCobranza.getPersona() != null) {
            Integer personaId = documentoCobranza.getPersona().getId();
            
            PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId).orElse(null);
            if (personaNatural != null) {
                String nombreCompleto = personaNatural.getNombres() + " " + 
                                       personaNatural.getApellidosPaterno() + " " + personaNatural.getApellidosMaterno();
                documentoCobranzaResponseDTO.setClienteNombre(nombreCompleto);
                documentoCobranzaResponseDTO.setClienteDocumento(personaNatural.getDocumento());
                documentoCobranzaResponseDTO.setTipoDocumentoCliente("DNI");
            } else {
                PersonaJuridica personaJuridica = personaJuridicaRepository.findByPersonasId(personaId).orElse(null);
                if (personaJuridica != null) {
                    documentoCobranzaResponseDTO.setClienteNombre(personaJuridica.getRazonSocial());
                    documentoCobranzaResponseDTO.setClienteDocumento(personaJuridica.getRuc());
                    documentoCobranzaResponseDTO.setTipoDocumentoCliente("RUC");
                }
            }
        }
        if (documentoCobranza.getSucursal() != null)
            documentoCobranzaResponseDTO.setSucursalDescripcion(documentoCobranza.getSucursal().getDescripcion());
        if (documentoCobranza.getFormaPago() != null)
            documentoCobranzaResponseDTO.setFormaPagoDescripcion(documentoCobranza.getFormaPago().getDescripcion());
        return documentoCobranzaResponseDTO;
    }
}