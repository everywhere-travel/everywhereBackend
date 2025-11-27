package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DetalleCotizacionService;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final FormaPagoRepository formaPagoRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final CounterRepository counterRepository;
    private final SucursalRepository sucursalRepository;
    private final CarpetaRepository carpetaRepository;
    private final PersonaRepository personasRepository;
    private final DetalleCotizacionService detalleCotizacionService; 

    @Override
    public CotizacionResponseDto create(CotizacionRequestDto cotizacionRequestDto, Integer personaId) {

        Cotizacion cotizacion = cotizacionMapper.toEntity(cotizacionRequestDto);
        cotizacion.setCodigoCotizacion(generateCodigoCotizacion());

        if (personaId == null) throw new DataIntegrityViolationException("PersonaId es obligatorio para crear una cotización");
        
        Personas persona = personasRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id " + personaId));
        cotizacion.setPersonas(persona);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id " + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    @Override
    public CotizacionResponseDto findById(Integer id) {
        return cotizacionRepository.findById(id).map(cotizacionMapper::toResponse)
            .orElseThrow(()-> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
    }

    @Override
    public List<CotizacionResponseDto> findAll() {
        return mapToResponseList(cotizacionRepository.findAll());
    }

    @Override
    public CotizacionResponseDto update(Integer id, CotizacionRequestDto cotizacionRequestDto) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        cotizacionMapper.updateEntityFromRequest(cotizacion, cotizacionRequestDto);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id " + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    @Override
    public void delete(Integer id) {
        if (!cotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + id);

        try {
            cotizacionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("No se puede eliminar esta cotización porque tiene dependencias asociadas (detalles de cotización, documentos u otros registros). Elimine primero las dependencias relacionadas.");
        } catch (Exception e) {
            throw new BadRequestException("Error al eliminar la cotización: " + e.getMessage());
        }
    }

    private String generateCodigoCotizacion() {
        Integer maxId = cotizacionRepository.findMaxId();
        int next = (maxId != null ? maxId + 1 : 1);
        return String.format("COT-%03d", next);
    }

    @Override
    public CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        CotizacionResponseDto cotizacionDTO = cotizacionMapper.toResponse(cotizacion);
        List<DetalleCotizacionResponseDto> detallesCompletos = detalleCotizacionService.findByCotizacionId(id);
        List<DetalleCotizacionSimpleDTO> detallesSimples = detallesCompletos.stream().map(cotizacionMapper::toDetalleSimple).toList();
        return cotizacionMapper.toResponseWithDetalles(cotizacionDTO, detallesSimples);
    }

    @Override
    public List<CotizacionResponseDto> findCotizacionesSinLiquidacion() {
        return mapToResponseList(cotizacionRepository.findCotizacionesSinLiquidacion());
    }

    private List<CotizacionResponseDto> mapToResponseList(List<Cotizacion> cotizaciones) {
        return cotizaciones.stream().map(cotizacionMapper::toResponse).toList();
    }

    @Override
    public ByteArrayInputStream generateDocx(Integer cotizacionId) {
        // Obtener la cotización con todos sus detalles
        CotizacionConDetallesResponseDTO cotizacion = findByIdWithDetalles(cotizacionId);
        
        if (cotizacion == null) {
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);
        }

        try {
            // Cargar la plantilla DOCX existente
            String templatePath = "/static/documents/PLANTILLA.docx";
            InputStream templateStream = getClass().getResourceAsStream(templatePath);
            
            if (templateStream == null) {
                throw new ResourceNotFoundException("No se encontró la plantilla en: " + templatePath);
            }

            // Abrir la plantilla como documento
            XWPFDocument document = new XWPFDocument(templateStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Agregar contenido del documento - Título de la cotización
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            titleParagraph.setSpacingBefore(300);
            titleParagraph.setSpacingAfter(300);
            
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("COTIZACIÓN N° " + cotizacion.getCodigoCotizacion());
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            // Agregar varias hojas de ejemplo (saltos de página)
            for (int i = 1; i <= 3; i++) {
                // Crear párrafo con contenido de ejemplo
                XWPFParagraph contentParagraph = document.createParagraph();
                contentParagraph.setAlignment(ParagraphAlignment.LEFT);
                contentParagraph.setSpacingBefore(200);
                
                XWPFRun contentRun = contentParagraph.createRun();
                contentRun.setText("Contenido de la página " + i);
                contentRun.setFontSize(12);
                
                // Si no es la última página, agregar salto de página
                if (i < 3) {
                    contentRun.addBreak(BreakType.PAGE);
                }
            }

            document.write(out);
            document.close();
            templateStream.close();

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al generar el DOCX de la cotización: " + e.getMessage());
        }
    }
}