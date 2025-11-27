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
import java.math.BigDecimal;
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

            // 1. Título de la cotización
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            titleParagraph.setSpacingBefore(300);
            titleParagraph.setSpacingAfter(300);
            
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("COTIZACIÓN N° " + cotizacion.getCodigoCotizacion());
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            // 2. Información general de la cotización
            addCotizacionInfo(document, cotizacion);

            // 3. Tabla de detalles de la cotización
            addDetallesTable(document, cotizacion);

            // 4. Sección de Importe a Pagar
            addImporteAPagar(document, cotizacion);

            // 5. Condiciones de Tarifa
            addCondicionesTarifa(document);

            // 6. Política de Privacidad
            addPoliticaPrivacidad(document);

            document.write(out);
            document.close();
            templateStream.close();

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al generar el DOCX de la cotización: " + e.getMessage());
        }
    }

    private void addCotizacionInfo(XWPFDocument document, CotizacionConDetallesResponseDTO cotizacion) {
        XWPFParagraph infoParagraph = document.createParagraph();
        infoParagraph.setSpacingBefore(200);
        infoParagraph.setSpacingAfter(200);
        
        XWPFRun infoRun = infoParagraph.createRun();
        infoRun.setText("Código: " + cotizacion.getCodigoCotizacion());
        infoRun.addBreak();
        infoRun.setText("Fecha Emisión: " + (cotizacion.getFechaEmision() != null ? cotizacion.getFechaEmision().toString() : "N/A"));
        infoRun.addBreak();
        String clienteInfo = "N/A";
        if (cotizacion.getPersonas() != null) {
            clienteInfo = cotizacion.getPersonas().getEmail() != null ? cotizacion.getPersonas().getEmail() : "ID: " + cotizacion.getPersonas().getId();
        }
        infoRun.setText("Cliente: " + clienteInfo);
        infoRun.addBreak();
        infoRun.setText("Destino: " + (cotizacion.getOrigenDestino() != null ? cotizacion.getOrigenDestino() : "N/A"));
        infoRun.addBreak();
        infoRun.setText("Adultos: " + cotizacion.getCantAdultos() + " | Niños: " + cotizacion.getCantNinos());
        infoRun.setFontSize(11);
    }

    private void addDetallesTable(XWPFDocument document, CotizacionConDetallesResponseDTO cotizacion) {
        if (cotizacion.getDetalles() == null || cotizacion.getDetalles().isEmpty()) {
            return;
        }

        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // Encabezados de la tabla
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("Descripción");
        headerRow.addNewTableCell().setText("Cantidad");
        headerRow.addNewTableCell().setText("Precio Unit.");
        headerRow.addNewTableCell().setText("Total");

        // Agregar detalles
        for (DetalleCotizacionSimpleDTO detalle : cotizacion.getDetalles()) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(detalle.getDescripcion() != null ? detalle.getDescripcion() : "");
            row.getCell(1).setText(detalle.getCantidad() != null ? detalle.getCantidad().toString() : "0");
            row.getCell(2).setText(detalle.getPrecioHistorico() != null ? String.format("%.2f", detalle.getPrecioHistorico()) : "0.00");
            
            // Calcular total: cantidad * precio
            BigDecimal total = BigDecimal.ZERO;
            if (detalle.getCantidad() != null && detalle.getPrecioHistorico() != null) {
                total = detalle.getPrecioHistorico().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            }
            row.getCell(3).setText(String.format("%.2f", total));
        }

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    private void addImporteAPagar(XWPFDocument document, CotizacionConDetallesResponseDTO cotizacion) {
        // Título "Pagos"
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(300);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("Pagos");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        // Calcular total
        double total = 0.0;
        if (cotizacion.getDetalles() != null) {
            for (DetalleCotizacionSimpleDTO detalle : cotizacion.getDetalles()) {
                if (detalle.getCantidad() != null && detalle.getPrecioHistorico() != null) {
                    total += detalle.getCantidad() * detalle.getPrecioHistorico().doubleValue();
                }
            }
        }

        // Crear tabla para "Importe a Pagar"
        XWPFTable table = document.createTable(2, 1);
        table.setWidth("50%");

        // Fila superior roja: IMPORTE A PAGAR
        XWPFTableRow row1 = table.getRow(0);
        XWPFTableCell cell1 = row1.getCell(0);
        cell1.setColor("DC2626"); // Rojo
        XWPFParagraph p1 = cell1.getParagraphs().get(0);
        XWPFRun r1 = p1.createRun();
        r1.setText("IMPORTE A PAGAR");
        r1.setBold(true);
        r1.setColor("FFFFFF"); // Blanco

        // Fila inferior: Detalles
        XWPFTableRow row2 = table.getRow(1);
        XWPFTableCell cell2 = row2.getCell(0);
        XWPFParagraph p2 = cell2.getParagraphs().get(0);
        XWPFRun r2 = p2.createRun();
        r2.setText("USD. " + String.format("%.2f", total));
        r2.setBold(true);
        r2.setFontSize(14);
        r2.addBreak();
        int totalPersonas = cotizacion.getCantAdultos() + cotizacion.getCantNinos();
        r2.setText("Precio por " + totalPersonas + " persona(s) en Tarifa: ___________");
        r2.setBold(false);
        r2.setFontSize(10);

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    private void addCondicionesTarifa(XWPFDocument document) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(200);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("CONDICIONES DE TARIFA:");
        titleRun.setBold(true);
        titleRun.setFontSize(12);

        String[] condiciones = {
            "Tarifa \"NO REEMBOLSABLE\", no permite devoluciones.",
            "Tarifas sujetas a cambios acorde a disponibilidad de la misma.",
            "Puede realizar cambios antes de la fecha de viaje inicial contratada hasta 1 día antes de ese viaje. Después de ese plazo, hasta 6 horas antes del vuelo, los cambios deben realizarse directamente con la línea aérea. No se permiten cambios después de este período.",
            "Cualquier modificación está sujeta al pago de una penalidad de $ por persona, así como un cargo de reemisión $35 y diferencias de tarifa, si las hay."
        };

        for (String condicion : condiciones) {
            XWPFParagraph condicionParagraph = document.createParagraph();
            condicionParagraph.setIndentationLeft(400);
            XWPFRun condicionRun = condicionParagraph.createRun();
            condicionRun.setText("• " + condicion);
            condicionRun.setFontSize(10);
        }

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    private void addPoliticaPrivacidad(XWPFDocument document) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(300);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("POLÍTICA DE PRIVACIDAD");
        titleRun.setBold(true);
        titleRun.setFontSize(12);

        String[] parrafos = {
            "EVERYWHERE TRAVEL S.A.C. te informa sobre su Política de Privacidad respecto al tratamiento y protección de los datos de carácter personal en los usuarios y clientes que puedan ser recabados por la navegación o contratación de servicios a través de cualquier medio digital o del sitio Web https://everywhereviajes.com/.",
            "En este sentido, el Titular garantiza el cumplimiento de la normativa vigente en materia de protección de datos personales, reflejada en la Ley Orgánica 3/2018, de 5 de diciembre, de Protección de Datos Personales y de Garantía de Derechos Digitales (LOPD GDD). Cumple también con el Reglamento (UE) 2016/679 del Parlamento Europeo y del Consejo de 27 de abril de 2016 relativo a la protección de las personas físicas (RGPD).",
            "El uso de sitio Web y él envió de la foto del DNI o Pasaporte implica la aceptación de esta Política de Privacidad, así como las condiciones incluidas en el Aviso Legal."
        };

        for (String parrafo : parrafos) {
            XWPFParagraph p = document.createParagraph();
            p.setSpacingAfter(100);
            XWPFRun r = p.createRun();
            r.setText(parrafo);
            r.setFontSize(9);
        }

        // Términos y Condiciones
        XWPFParagraph terminosTitleParagraph = document.createParagraph();
        terminosTitleParagraph.setSpacingBefore(100);
        XWPFRun terminosTitleRun = terminosTitleParagraph.createRun();
        terminosTitleRun.setText("Términos y Condiciones de uso, el usuario y/o cliente reconoce que cumple lo siguiente:");
        terminosTitleRun.setBold(true);
        terminosTitleRun.setFontSize(10);

        String[] terminos = {
            "Es mayor de edad, conforme a la Ley peruana vigente. Si es menor de edad, por favor abstenerse de utilizar la herramienta.",
            "Es hábil en el idioma español.",
            "Se encuentra en pleno uso de sus facultades mentales y no adolece de vicio que afecte sus razonamiento, entendimiento y manifestación de voluntad. Por lo tanto, tiene plena capacidad civil, de acuerdo con la Ley peruana vigente.",
            "Ha leído íntegramente los Términos y Condiciones de uso.",
            "Actúa y declara únicamente por sí mismo y no en representación de terceros ni de menores de edad conforme a la Ley peruana vigente."
        };

        for (int i = 0; i < terminos.length; i++) {
            XWPFParagraph terminoParagraph = document.createParagraph();
            terminoParagraph.setIndentationLeft(400);
            XWPFRun terminoRun = terminoParagraph.createRun();
            terminoRun.setText((i + 1) + ". " + terminos[i]);
            terminoRun.setFontSize(9);
        }
    }
}