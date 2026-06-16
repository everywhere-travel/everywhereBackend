package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ReciboMapper;
import com.everywhere.backend.model.entity.Recibo;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.repository.DetalleReciboRepository;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.ReciboRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.NaturalJuridicoRepository;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.AsientoContableService;
import com.everywhere.backend.service.ReciboService;
import lombok.RequiredArgsConstructor;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReciboServiceImpl implements ReciboService {

    private final ReciboRepository reciboRepository;
    private final DetalleReciboRepository detalleReciboRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final ReciboMapper reciboMapper;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final SucursalRepository sucursalRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final NaturalJuridicoRepository naturalJuridicoRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final CarpetaRepository carpetaRepository;
    private final com.everywhere.backend.util.pdf.ReciboPdfGenerator reciboPdfGenerator;
    private final DocumentoCobranzaRepository documentoCobranzaRepository;
    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final AsientoContableService asientoContableService;

    @Override
    @Transactional
    public ReciboResponseDTO createRecibo(Integer documentoCobranzaId, Integer personaJuridicaId, Integer sucursalId, BigDecimal montoPago) {
        if (documentoCobranzaId == null) {
            throw new IllegalArgumentException("El ID del documento de cobranza no puede ser nulo");
        }

        Long documentoId = documentoCobranzaId.longValue();

        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado con ID: " + documentoCobranzaId));

        String[] serieCorrelativo = generateNextDocumentNumber();

        Recibo recibo = new Recibo();
        recibo.setSerie(serieCorrelativo[0]);
        recibo.setCorrelativo(Integer.parseInt(serieCorrelativo[1]));

        recibo.setDocumentoCobranza(documentoCobranza);
        recibo.setCotizacion(documentoCobranza.getCotizacion());
        recibo.setPersona(documentoCobranza.getPersona());
        recibo.setPersonaJuridica(documentoCobranza.getPersonaJuridica());
        recibo.setSucursal(documentoCobranza.getSucursal());
        recibo.setFormaPago(documentoCobranza.getFormaPago());
        recibo.setMoneda(documentoCobranza.getMoneda());
        recibo.setCarpeta(documentoCobranza.getCarpeta());
        recibo.setFechaEmision(documentoCobranza.getFechaEmision());

        if (personaJuridicaId != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Persona jurídica no encontrada con ID: " + personaJuridicaId));
            recibo.setPersonaJuridica(personaJuridica);
        }

        if (sucursalId != null) {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + sucursalId));
            recibo.setSucursal(sucursal);
        }

        recibo = reciboRepository.save(recibo);

        crearDetallesDesdeDocumentoCobranza(recibo, documentoId, montoPago, documentoCobranza);

        // Generar asiento contable: Caja/Banco (DEBE) vs Clientes (HABER)
        asientoContableService.generarAsientoPorRecibo(recibo);

        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public ByteArrayInputStream generatePdf(Integer reciboId) {
        Recibo recibo = reciboRepository.findByIdWithRelations(reciboId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recibo no encontrado con ID: " + reciboId));

        reciboRepository.findByIdWithDetalles(reciboId)
                .ifPresent(r -> recibo.setDetalleRecibo(r.getDetalleRecibo()));

        ReciboResponseDTO reciboResponseDTO = reciboMapper.toResponseDTO(recibo);
        String userName = getAuthenticatedUserName();
        return reciboPdfGenerator.generatePdf(reciboResponseDTO, userName);
    }

    @Override
    public ReciboResponseDTO findById(Integer id) {
        Recibo recibo = reciboRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado con ID: " + id));
        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public ReciboResponseDTO findBySerieAndCorrelativo(String serie, Integer correlativo) {
        Recibo recibo = reciboRepository.findBySerieAndCorrelativo(serie, correlativo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recibo no encontrado con serie: " + serie + " y correlativo: " + correlativo));
        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public List<ReciboResponseDTO> findAll() {
        return mapToResponseList(reciboRepository.findAllForListing());
    }

    @Override
    public List<ReciboResponseDTO> findByDocumentoCobranzaId(Integer documentoCobranzaId) {
        Long documentoId = documentoCobranzaId.longValue();
        List<Recibo> recibos = reciboRepository.findByDocumentoCobranzaId(documentoId);
        if (recibos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron recibos para el documento de cobranza ID: " + documentoCobranzaId);
        }
        return mapToResponseList(recibos);
    }

    @Override
    public List<ReciboResponseDTO> findByCotizacionId(Integer cotizacionId) {
        List<Recibo> recibos = reciboRepository.findByCotizacionId(cotizacionId);
        if (recibos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron recibos para la cotización ID: " + cotizacionId);
        }
        return mapToResponseList(recibos);
    }

    @Override
    @Transactional
    public ReciboResponseDTO patchRecibo(Integer id, ReciboUpdateDTO reciboUpdateDTO) {
        if (!reciboRepository.existsById(id))
            throw new ResourceNotFoundException("Recibo no encontrado con ID: " + id);

        if (reciboUpdateDTO.getDetalleDocumentoId() != null &&
                !detalleDocumentoRepository.existsById(reciboUpdateDTO.getDetalleDocumentoId()))
            throw new ResourceNotFoundException(
                    "Detalle de documento no encontrado con ID: " + reciboUpdateDTO.getDetalleDocumentoId());

        Recibo recibo = reciboRepository.findById(id).get();
        reciboMapper.updateEntityFromUpdateDTO(recibo, reciboUpdateDTO);

        // Lógica mutuamente excluyente: DetalleDocumento XOR PersonaJuridica
        if (reciboUpdateDTO.getDetalleDocumentoId() != null) {
            DetalleDocumento detalleDocumento = detalleDocumentoRepository
                    .findById(reciboUpdateDTO.getDetalleDocumentoId()).get();
            recibo.setDetalleDocumento(detalleDocumento);
            recibo.setPersonaJuridica(null);
        } else if (reciboUpdateDTO.getPersonaJuridicaId() != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository
                    .findById(reciboUpdateDTO.getPersonaJuridicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: "
                            + reciboUpdateDTO.getPersonaJuridicaId()));

            if (recibo.getPersona() != null) {
                Integer personaId = recibo.getPersona().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(),
                                    reciboUpdateDTO.getPersonaJuridicaId())
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException(
                                "La persona jurídica ID " + reciboUpdateDTO.getPersonaJuridicaId() +
                                        " no está asociada a la persona natural del recibo");
                    }
                }
            }

            recibo.setPersonaJuridica(personaJuridica);
            recibo.setDetalleDocumento(null);
        }

        // Validar y actualizar Sucursal
        if (reciboUpdateDTO.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(reciboUpdateDTO.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + reciboUpdateDTO.getSucursalId()));
            recibo.setSucursal(sucursal);
        }

        // Validar y actualizar FormaPago
        if (reciboUpdateDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(reciboUpdateDTO.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con ID: " + reciboUpdateDTO.getFormaPagoId()));
            recibo.setFormaPago(formaPago);
        }

        return reciboMapper.toResponseDTO(reciboRepository.save(recibo));
    }

    // ========== MÉTODOS PRIVADOS ==========
    private String[] generateNextDocumentNumber() {
        Optional<Recibo> lastReciboOpt = reciboRepository.findTopByOrderByIdDesc();

        if (lastReciboOpt.isPresent()) {
            Recibo lastRecibo = lastReciboOpt.get();
            String lastSerie = lastRecibo.getSerie();
            Integer lastCorrelativo = lastRecibo.getCorrelativo();

            if (lastSerie != null && lastCorrelativo != null) {
                int nextCorrelativo = lastCorrelativo + 1;
                return new String[] { lastSerie, String.valueOf(nextCorrelativo) };
            }
        }

        // Primer recibo: serie R01, correlativo 1
        return new String[] { "R01", "1" };
    }

    @Transactional
    private void crearDetallesDesdeDocumentoCobranza(Recibo recibo, Long documentoCobranzaId, BigDecimal montoPago, DocumentoCobranza documentoCobranza) {
        if (montoPago != null && montoPago.compareTo(BigDecimal.ZERO) > 0) {
            DetalleRecibo detalleRecibo = new DetalleRecibo();
            detalleRecibo.setRecibo(recibo);
            detalleRecibo.setCantidad(1);
            
            String serie = documentoCobranza.getSerie() != null ? documentoCobranza.getSerie() + "-" : "";
            String correlativo = documentoCobranza.getCorrelativo() != null ? String.valueOf(documentoCobranza.getCorrelativo()) : "S/N";
            detalleRecibo.setDescripcion("Pago a cuenta de Documento de Cobranza " + serie + correlativo);
            
            detalleRecibo.setPrecio(montoPago);
            detalleReciboRepository.save(detalleRecibo);
        } else {
            List<DetalleDocumentoCobranza> detallesDocumento = detalleDocumentoCobranzaRepository
                    .findByDocumentoCobranzaId(documentoCobranzaId);

            for (DetalleDocumentoCobranza detalleDocumento : detallesDocumento) {
                DetalleRecibo detalleRecibo = new DetalleRecibo();

                detalleRecibo.setRecibo(recibo);
                detalleRecibo.setCantidad(detalleDocumento.getCantidad() != null ? detalleDocumento.getCantidad() : 1);
                detalleRecibo.setDescripcion(detalleDocumento.getDescripcion());
                detalleRecibo
                        .setPrecio(detalleDocumento.getPrecio() != null ? detalleDocumento.getPrecio() : BigDecimal.ZERO);

                if (detalleDocumento.getProducto() != null) {
                    detalleRecibo.setProducto(detalleDocumento.getProducto());
                }

                detalleReciboRepository.save(detalleRecibo);
            }
        }
    }

    private List<ReciboResponseDTO> mapToResponseList(List<Recibo> recibos) {
        return recibos.stream().map(reciboMapper::toResponseDTO).toList();
    }

    private String getAuthenticatedUserName() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof UserPrincipal) {

                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                if (userPrincipal.getUser() != null && userPrincipal.getUser().getNombre() != null) {
                    return userPrincipal.getUser().getNombre();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el usuario autenticado: " + e.getMessage());
        }

        return "Usuario desconocido";
    }

    // ========== GESTIÓN DE CARPETAS ==========

    @Override
    public List<ReciboResponseDTO> findByCarpeta(Integer carpetaId) {
        return mapToResponseList(reciboRepository.findByCarpetaId(carpetaId));
    }

    @Override
    public List<ReciboResponseDTO> findSinCarpeta() {
        return mapToResponseList(reciboRepository.findByCarpetaIsNull());
    }

    @Override
    @Transactional
    public ReciboResponseDTO updateCarpeta(Integer id, Integer carpetaId) {
        Recibo recibo = reciboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado con ID: " + id));

        if (carpetaId != null) {
            Carpeta carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));
            recibo.setCarpeta(carpeta);
        } else {
            recibo.setCarpeta(null);
        }

        return reciboMapper.toResponseDTO(reciboRepository.save(recibo));
    }

    @Override
    public BigDecimal calcularTotalPagado(Integer documentoCobranzaId) {
        Long documentoId = documentoCobranzaId.longValue();
        List<Recibo> recibos = reciboRepository.findByDocumentoCobranzaId(documentoId);

        BigDecimal totalPagado = BigDecimal.ZERO;
        for (Recibo recibo : recibos) {
            List<DetalleRecibo> detalles = detalleReciboRepository.findByReciboId(recibo.getId());
            for (DetalleRecibo detalle : detalles) {
                BigDecimal cantidad = detalle.getCantidad() != null
                        ? BigDecimal.valueOf(detalle.getCantidad())
                        : BigDecimal.ZERO;
                BigDecimal precio = detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO;
                totalPagado = totalPagado.add(cantidad.multiply(precio));
            }
        }
        return totalPagado;
    }
}
