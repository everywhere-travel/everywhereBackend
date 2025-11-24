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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XWPFDocument document = new XWPFDocument();

            // 1. Agregar encabezado con imagen al 100% del ancho
            addHeaderImage(document);
            
            // Establecer todos los márgenes del documento en 0 DESPUÉS de crear contenido
            CTSectPr sectPr = document.getDocument().getBody().isSetSectPr() 
                ? document.getDocument().getBody().getSectPr() 
                : document.getDocument().getBody().addNewSectPr();
            
            CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
            pageMar.setTop(BigInteger.ZERO);
            pageMar.setRight(BigInteger.ZERO);
            pageMar.setBottom(BigInteger.ZERO);
            pageMar.setLeft(BigInteger.ZERO);
            pageMar.setHeader(BigInteger.ZERO);
            pageMar.setFooter(BigInteger.ZERO);
            pageMar.setGutter(BigInteger.ZERO);

            // 2. Agregar contenido del documento con título centrado
            XWPFParagraph contentParagraph = document.createParagraph();
            contentParagraph.setAlignment(ParagraphAlignment.CENTER);
            contentParagraph.setSpacingBefore(200); // Espacio antes
            contentParagraph.setSpacingAfter(200);  // Espacio después
            
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("COTIZACIÓN: " + cotizacion.getCodigoCotizacion());
            contentRun.setBold(true);
            contentRun.setFontSize(16);

            // Agregar espacio
            document.createParagraph();

            // 3. Agregar pie de página con imagen al 100% del ancho
            addFooterImage(document);

            document.write(out);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al generar el DOCX de la cotización: " + e.getMessage());
        }
    }

    /**
     * Agrega la imagen del encabezado al documento ocupando el 100% del ancho
     */
    private void addHeaderImage(XWPFDocument document) {
        try {
            // ========== CONFIGURACIÓN DE TAMAÑO DEL ENCABEZADO ==========
            // Ajusta estos valores según necesites (base: 5400000 = 100% ancho A4)
            final double HEADER_WIDTH_PERCENTAGE = 1.43; // 145% del ancho de página
            final int BASE_PAGE_WIDTH_EMU = 5400000; // Ancho base de página A4 en EMUs
            final int HEADER_HEIGHT_EMU = 1450000; // Altura del encabezado en EMUs
            // ============================================================
            
            String headerImagePath = "/static/images/Encabezado.png";
            InputStream headerStream = getClass().getResourceAsStream(headerImagePath);
            
            if (headerStream == null) {
                System.err.println("Advertencia: No se encontró la imagen del encabezado en: " + headerImagePath);
                return;
            }

            XWPFParagraph headerParagraph = document.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.LEFT);  // Izquierda para que empiece desde el borde
            
            // Sin márgenes ni indentación para ocupar todo el ancho
            headerParagraph.setIndentationLeft(0);
            headerParagraph.setIndentationRight(0);
            headerParagraph.setSpacingBefore(0);
            headerParagraph.setSpacingAfter(0);
            
            XWPFRun headerRun = headerParagraph.createRun();
            
            // Calcular ancho final basado en el porcentaje configurado
            int finalWidthEMU = (int) (BASE_PAGE_WIDTH_EMU * HEADER_WIDTH_PERCENTAGE);
            headerRun.addPicture(headerStream, XWPFDocument.PICTURE_TYPE_PNG, "Encabezado.png", 
                                finalWidthEMU, HEADER_HEIGHT_EMU);
            
            headerStream.close();

        } catch (Exception e) {
            System.err.println("Error al agregar imagen del encabezado: " + e.getMessage());
        }
    }

    /**
     * Agrega la imagen del pie de página al documento ocupando el 100% del ancho
     */
    private void addFooterImage(XWPFDocument document) {
        try {
            // ========== CONFIGURACIÓN DE TAMAÑO DEL PIE DE PÁGINA ==========
            // Ajusta estos valores según necesites (base: 5400000 = 100% ancho A4)
            final double FOOTER_WIDTH_PERCENTAGE = 1.43; // 145% del ancho de página
            final int BASE_PAGE_WIDTH_EMU = 5400000; // Ancho base de página A4 en EMUs
            final int FOOTER_HEIGHT_EMU = 1160000; // Altura del pie de página en EMUs
            // ==============================================================
            
            String footerImagePath = "/static/images/pie.png";
            InputStream footerStream = getClass().getResourceAsStream(footerImagePath);
            
            if (footerStream == null) {
                System.err.println("Advertencia: No se encontró la imagen del pie de página en: " + footerImagePath);
                return;
            }

            XWPFParagraph footerParagraph = document.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.LEFT);  // Izquierda para que empiece desde el borde
            
            // Sin márgenes ni indentación para ocupar todo el ancho
            footerParagraph.setIndentationLeft(0);
            footerParagraph.setIndentationRight(0);
            footerParagraph.setSpacingBefore(0);
            footerParagraph.setSpacingAfter(0);
            
            XWPFRun footerRun = footerParagraph.createRun();
            
            // Calcular ancho final basado en el porcentaje configurado
            int finalWidthEMU = (int) (BASE_PAGE_WIDTH_EMU * FOOTER_WIDTH_PERCENTAGE);
            footerRun.addPicture(footerStream, XWPFDocument.PICTURE_TYPE_PNG, "pie.png", 
                                finalWidthEMU, FOOTER_HEIGHT_EMU);
            
            footerStream.close();

        } catch (Exception e) {
            System.err.println("Error al agregar imagen del pie: " + e.getMessage());
        }
    }
}