package com.everywhere.backend.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.io.image.ImageDataFactory;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleDocumentoCobranzaMapper;
import com.everywhere.backend.mapper.DocumentoCobranzaMapper;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.security.UserPrincipal;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoCobranzaServiceImpl implements DocumentoCobranzaService {

    private final CotizacionService cotizacionService;
    private final DocumentoCobranzaRepository documentoCobranzaRepository;
    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DocumentoCobranzaMapper documentoCobranzaMapper;
    private final DetalleDocumentoCobranzaMapper detalleDocumentoCobranzaMapper;
    private final com.everywhere.backend.repository.PersonaJuridicaRepository personaJuridicaRepository;
    private final com.everywhere.backend.repository.SucursalRepository sucursalRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final com.everywhere.backend.repository.NaturalJuridicoRepository naturalJuridicoRepository;
    private final com.everywhere.backend.repository.PersonaNaturalRepository personaNaturalRepository;
    private final com.everywhere.backend.service.DetalleCotizacionService detalleCotizacionService;

    @Override
    @Transactional
    public DocumentoCobranzaResponseDTO createDocumentoCobranza(Integer cotizacionId, Integer personaJuridicaId,
            Integer sucursalId) {
        if (cotizacionId == null)
            throw new IllegalArgumentException("El ID de la cotización no puede ser nulo");

        if (documentoCobranzaRepository.findByCotizacionId(cotizacionId).isPresent())
            throw new DataIntegrityViolationException(
                    "Ya existe un documento de cobranza para la cotización ID: " + cotizacionId);

        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);

        String numeroDocumento = generateNextDocumentNumber();
        DocumentoCobranza documentoCobranza = documentoCobranzaMapper.fromCotizacion(cotizacion, numeroDocumento);

        // Validar y setear PersonaJuridica si fue proporcionada
        if (personaJuridicaId != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Persona jurídica no encontrada con ID: " + personaJuridicaId));

            // Validar que la PersonaJuridica esté asociada a la PersonaNatural de la
            // cotización
            if (cotizacion.getPersonas() != null) {
                Integer personaId = cotizacion.getPersonas().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    // Verificar que existe la relación NaturalJuridico
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(), personaJuridicaId)
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException("La persona jurídica ID " + personaJuridicaId +
                                " no está asociada a la persona natural de la cotización");
                    }
                }
            }

            documentoCobranza.setPersonaJuridica(personaJuridica);
        }

        // Validar y setear Sucursal si fue proporcionada
        if (sucursalId != null) {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
            documentoCobranza.setSucursal(sucursal);
        }

        documentoCobranza = documentoCobranzaRepository.save(documentoCobranza);

        // Crear detalles desde cotización con repartición por cantidad
        crearDetallesDesdeCotizacion(documentoCobranza, cotizacionId);

        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public ByteArrayInputStream generatePdf(Long documentoId) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByIdWithRelations(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado con ID: " + documentoId));

        documentoCobranzaRepository.findByIdWithDetalles(documentoId)
                .ifPresent(d -> documentoCobranza.setDetalles(d.getDetalles()));
        DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO = documentoCobranzaMapper
                .toResponseDTO(documentoCobranza);
        return generatePdfFromDTO(documentoCobranzaResponseDTO);
    }

    @Override
    public DocumentoCobranzaResponseDTO findById(Long id) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public DocumentoCobranzaResponseDTO findByNumero(String numero) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado con número: " + numero));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public List<DocumentoCobranzaResponseDTO> findAll() {
        return mapToResponseList(documentoCobranzaRepository.findAllWithRelations());
    }

    @Override
    public DocumentoCobranzaResponseDTO findByCotizacionId(Integer cotizacionId) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByCotizacionId(cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado para cotización ID: " + cotizacionId));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    @Transactional
    public DocumentoCobranzaResponseDTO patchDocumento(Long id, DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        if (!documentoCobranzaRepository.existsById(id))
            throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id);

        if (documentoCobranzaUpdateDTO.getDetalleDocumentoId() != null &&
                !detalleDocumentoRepository.existsById(documentoCobranzaUpdateDTO.getDetalleDocumentoId()))
            throw new ResourceNotFoundException(
                    "Detalle de documento no encontrado con ID: " + documentoCobranzaUpdateDTO.getDetalleDocumentoId());

        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(id).get();
        documentoCobranzaMapper.updateEntityFromUpdateDTO(documentoCobranza, documentoCobranzaUpdateDTO);

        // ========== LÓGICA MUTUAMENTE EXCLUYENTE: DetalleDocumento XOR PersonaJuridica
        // ==========
        // Si se envía detalleDocumentoId, usar documento personal y LIMPIAR empresa
        if (documentoCobranzaUpdateDTO.getDetalleDocumentoId() != null) {
            DetalleDocumento detalleDocumento = detalleDocumentoRepository
                    .findById(documentoCobranzaUpdateDTO.getDetalleDocumentoId()).get();
            documentoCobranza.setDetalleDocumento(detalleDocumento);
            // Limpiar PersonaJuridica cuando se selecciona documento personal
            documentoCobranza.setPersonaJuridica(null);
        }
        // Si se envía personaJuridicaId, usar empresa y LIMPIAR documento personal
        else if (documentoCobranzaUpdateDTO.getPersonaJuridicaId() != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository
                    .findById(documentoCobranzaUpdateDTO.getPersonaJuridicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: "
                            + documentoCobranzaUpdateDTO.getPersonaJuridicaId()));

            // Validar que la PersonaJuridica esté asociada a la PersonaNatural del
            // documento
            if (documentoCobranza.getPersona() != null) {
                Integer personaId = documentoCobranza.getPersona().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(),
                                    documentoCobranzaUpdateDTO.getPersonaJuridicaId())
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException(
                                "La persona jurídica ID " + documentoCobranzaUpdateDTO.getPersonaJuridicaId() +
                                        " no está asociada a la persona natural del documento");
                    }
                }
            }

            documentoCobranza.setPersonaJuridica(personaJuridica);
            // Limpiar DetalleDocumento cuando se selecciona empresa
            documentoCobranza.setDetalleDocumento(null);
        }

        // Validar y actualizar Sucursal si fue proporcionada
        if (documentoCobranzaUpdateDTO.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(documentoCobranzaUpdateDTO.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + documentoCobranzaUpdateDTO.getSucursalId()));
            documentoCobranza.setSucursal(sucursal);
        }
        
        // Validar y actualizar FormaPago si fue proporcionada
        if (documentoCobranzaUpdateDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(documentoCobranzaUpdateDTO.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con ID: " + documentoCobranzaUpdateDTO.getFormaPagoId()));
            documentoCobranza.setFormaPago(formaPago);
            System.out.println("✅ FormaPago actualizada: " + formaPago.getDescripcion());
        }

        return documentoCobranzaMapper.toResponseDTO(documentoCobranzaRepository.save(documentoCobranza));
    }

    // ========== MÉTODOS PRIVADOS ==========
    private String generateNextDocumentNumber() {
        Optional<String> lastNumberOpt = documentoCobranzaRepository.findLastDocumentNumber();

        if (lastNumberOpt.isPresent()) {
            String lastNumber = lastNumberOpt.get();
            String[] parts = lastNumber.split("-");
            if (parts.length == 2) {
                String serie = parts[0];
                int numero = Integer.parseInt(parts[1]);
                int nextNumero = numero + 1;

                if (nextNumero > 999999999)
                    return "DC02-000000001";

                return String.format("%s-%09d", serie, nextNumero);
            }
        }
        return "DC01-000000300";
    }

    private String convertirNumeroALetras(double numero, String moneda) {
        if (numero == 0)
            return "Cero " + obtenerNombreMoneda(moneda);

        // Separar parte entera y decimales
        long parteEntera = (long) numero;
        int decimales = (int) Math.round((numero - parteEntera) * 100);

        String parteEnteraEnLetras = convertirEnteroALetras(parteEntera);
        String nombreMoneda = obtenerNombreMoneda(moneda);

        // Convertir todo a minúsculas y capitalizar solo la primera letra
        String resultado;
        if (decimales == 0)
            resultado = parteEnteraEnLetras + " con 00/100 " + nombreMoneda;
        else
            resultado = parteEnteraEnLetras + " con " + String.format("%02d", decimales) + "/100 " + nombreMoneda;

        // Convertir a minúsculas y capitalizar solo la primera letra
        resultado = resultado.toLowerCase();
        if (resultado.length() > 0)
            resultado = Character.toUpperCase(resultado.charAt(0)) + resultado.substring(1);

        return resultado;
    }

    private String obtenerNombreMoneda(String moneda) {
        if (moneda == null)
            return "dólares americanos";

        switch (moneda.toUpperCase()) {
            case "USD":
                return "dólares americanos";
            case "PEN":
                return "soles";
            case "EUR":
                return "euros";
            default:
                return "dólares americanos";
        }
    }

    private String convertirEnteroALetras(long numero) {
        if (numero == 0)
            return "CERO";
        if (numero == 1)
            return "UNO";

        String resultado = "";
        // Millones
        if (numero >= 1000000) {
            long millones = numero / 1000000;
            if (millones == 1)
                resultado += "UN MILLÓN ";
            else
                resultado += convertirGrupoTresCifras((int) millones) + " MILLONES ";
            numero %= 1000000;
        }

        // Miles
        if (numero >= 1000) {
            long miles = numero / 1000;
            if (miles == 1)
                resultado += "MIL ";
            else
                resultado += convertirGrupoTresCifras((int) miles) + " MIL ";
            numero %= 1000;
        }
        // Centenas, decenas y unidades
        if (numero > 0)
            resultado += convertirGrupoTresCifras((int) numero);
        return resultado.trim();
    }

    private String convertirGrupoTresCifras(int numero) {
        if (numero == 0)
            return "";

        String[] unidades = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE" };
        String[] especiales = { "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE",
                "DIECIOCHO", "DIECINUEVE" };
        String[] decenas = { "", "", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA",
                "NOVENTA" };
        String[] centenas = { "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS",
                "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };

        String resultado = "";

        // Centenas
        int c = numero / 100;
        if (c > 0) {
            if (numero == 100)
                resultado += "CIEN";
            else
                resultado += centenas[c] + " ";
        }

        // Decenas y unidades
        int resto = numero % 100;
        if (resto >= 10 && resto <= 19)
            resultado += especiales[resto - 10];
        else {
            int d = resto / 10;
            int u = resto % 10;

            if (d == 2 && u > 0)
                resultado += "VEINTI" + unidades[u].toLowerCase();
            else {
                if (d > 0) {
                    resultado += decenas[d];
                    if (u > 0)
                        resultado += " Y " + unidades[u];
                } else if (u > 0)
                    resultado += unidades[u];
            }
        }
        return resultado.trim();
    }

    private ByteArrayInputStream generatePdfFromDTO(DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO) {
        if (documentoCobranzaResponseDTO == null)
            throw new ResourceNotFoundException("No se encontró el documento de cobranza para generar el PDF");

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            addCompanyHeader(document, documentoCobranzaResponseDTO); // 1. ENCABEZADO DE LA EMPRESA
            addSummaryTable(document, documentoCobranzaResponseDTO); // 2. TABLA DE RESUMEN (MONEDA, FILE, FORMA DE
                                                                     // PAGO)
            // 3. TABLA DE DETALLES DE SERVICIOS
            if (documentoCobranzaResponseDTO.getDetalles() != null
                    && !documentoCobranzaResponseDTO.getDetalles().isEmpty())
                addServicesTable(document, documentoCobranzaResponseDTO);
            addObservationsBox(document, documentoCobranzaResponseDTO); // 4. CUADRO DE OBSERVACIONES (separado)
            addPageFooter(pdfDocument); // 5. PIE DE PÁGINA EN CADA HOJA

            // ✅ NUEVO: Agregar texto rojo en la última página ANTES de cerrar
            addRedTextToLastPage(document, pdfDocument);

            document.close();
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Ver el error completo
            throw new ResourceNotFoundException("Error al generar el PDF del documento de cobranza: " + e.getMessage());
        }
    }

    // 1. ENCABEZADO DE LA EMPRESA
    private void addCompanyHeader(Document document, DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO) {
        // Crear tabla principal para el encabezado
        Table headerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

        // Columna izquierda - Logo y datos de empresa en celdas separadas
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setWidth(UnitValue.createPercentValue(60));

        // 1. Celda del Logo/Imagen
        Table logoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setPadding(10)
                .setHeight(60);

        try {
            String logoPath = "/static/images/everyLogo.png";
            java.io.InputStream logoStream = getClass().getResourceAsStream(logoPath);

            byte[] logoBytes = logoStream.readAllBytes();
            Image logoImage = new Image(ImageDataFactory.create(logoBytes));

            logoImage.setWidth(150); // Ancho fijo en puntos
            logoImage.setHeight(50); // Alto fijo en puntos

            logoCell.add(logoImage);
            logoStream.close();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al cargar el logo para el PDF del documento de cobranza");
        }

        logoTable.addCell(logoCell);
        leftCell.add(logoTable);

        // Espacio entre celdas
        leftCell.add(new Paragraph("\n").setMarginTop(7));

        // 2. Celda única con información de la empresa (3 renglones)
        Table companyInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        Cell companyInfoCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);

        // Crear tabla interna para organizar la información en renglones
        Table innerCompanyTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

        // Renglón 1 - Nombre de la empresa
        innerCompanyTable.addCell(new Cell().add(new Paragraph("EVERYWHERE TRAVEL SAC").setBold().setFontSize(11))
                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
        // Renglón 2 - Dirección
        innerCompanyTable
                .addCell(new Cell().add(new Paragraph("MZ.J' LTE.10 URB.SOLILUZ, TRUJILLO, PERU").setFontSize(9))
                        .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
        // Renglón 3 - Teléfonos
        innerCompanyTable.addCell(new Cell().add(new Paragraph("Teléfono: 044 729-728").setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
        innerCompanyTable.addCell(new Cell().add(new Paragraph("Celular: +51 944 493 851 / 947 755 582").setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));

        companyInfoCell.add(innerCompanyTable);
        companyInfoTable.addCell(companyInfoCell).setBorder(Border.NO_BORDER);
        leftCell.add(companyInfoTable);

        leftCell.add(new Paragraph("\n").setMarginTop(7));

        // Columna derecha - Los dos cuadros apilados
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setWidth(UnitValue.createPercentValue(40));

        // Cuadro del RUC y DOCUMENTO DE COBRANZA
        Table rucTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

        Cell rucCell = new Cell().setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER).setPadding(8);
        rucCell.add(new Paragraph("R.U.C. Nº 20602292941").setFontSize(10).setBold());
        rucCell.add(new Paragraph("DOCUMENTO DE COBRANZA").setFontSize(14).setBold().setMarginTop(5));

        // Agregar número del documento de cobranza
        if (documentoCobranzaResponseDTO.getNumero() != null)
            rucCell.add(
                    new Paragraph(documentoCobranzaResponseDTO.getNumero()).setFontSize(12).setBold().setMarginTop(5));

        rucTable.addCell(rucCell);
        rightCell.add(rucTable);

        // Espacio reducido entre cuadros (75% menos que original)
        rightCell.add(new Paragraph("\n").setMarginTop(1));

        // Cuadro de información del cliente
        Table clientInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        Cell clientCell = new Cell().setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setPadding(8);

        String fechaEmision = documentoCobranzaResponseDTO.getFechaEmision() != null
                ? documentoCobranzaResponseDTO.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String nombreCliente = documentoCobranzaResponseDTO.getClienteNombre() != null
                ? documentoCobranzaResponseDTO.getClienteNombre().toUpperCase()
                : "CLIENTE";
        String numeroDocumento = documentoCobranzaResponseDTO.getClienteDocumento() != null
                ? documentoCobranzaResponseDTO.getClienteDocumento()
                : "00000000";
        String sucursalDescripcion = documentoCobranzaResponseDTO.getSucursalDescripcion() != null
                ? documentoCobranzaResponseDTO.getSucursalDescripcion()
                : "";

        // Crear una tabla interna de 4 filas y 2 columnas para organizar cada dato en
        // su propia fila
        Table innerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

        // Fila 1 - Solo Fecha de emisión
        innerTable.addCell(new Cell().add(new Paragraph("Fecha de emisión:").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(
                new Cell().add(new Paragraph(fechaEmision).setFontSize(9)).setBorder(Border.NO_BORDER).setPadding(1));

        // Fila 2 - Solo Señor(es)
        innerTable.addCell(new Cell().add(new Paragraph("Señor(es):").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(
                new Cell().add(new Paragraph(nombreCliente).setFontSize(9)).setBorder(Border.NO_BORDER).setPadding(1));

        // Fila 3 - Solo Documento
        innerTable.addCell(new Cell()
                .add(new Paragraph("Documento - " + documentoCobranzaResponseDTO.getTipoDocumentoCliente() + ":")
                        .setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(numeroDocumento).setFontSize(9)).setBorder(Border.NO_BORDER)
                .setPadding(1));

        // Fila 4 - Solo Sucursal
        innerTable.addCell(new Cell().add(new Paragraph("Sucursal:").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(sucursalDescripcion).setFontSize(9)).setBorder(Border.NO_BORDER)
                .setPadding(1));

        clientCell.add(innerTable);
        clientInfoTable.addCell(clientCell);
        rightCell.add(clientInfoTable);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("").setMarginTop(1));
    }

    // 2. TABLA DE RESUMEN (MONEDA, FILE, FORMA DE PAGO)
    private void addSummaryTable(Document document, DocumentoCobranzaResponseDTO documento) {
        // Tabla de resumen con 3 columnas en una sola fila sin líneas divisorias
        Table resumenTable = new Table(3).setWidth(UnitValue.createPercentValue(100));
        resumenTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        // Celda 1: Moneda - todo en una línea
        Cell monedaCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

        String moneda = documento.getMoneda() != null ? documento.getMoneda() + " - Dólar Americano"
                : "USD - Dólar Americano";

        monedaCell.add(new Paragraph("Moneda: " + moneda).setFontSize(10));

        // Celda 2: File - todo en una línea
        Cell fileCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

        String fileVenta = documento.getFileVenta() != null ? documento.getFileVenta() : "N/A";

        fileCell.add(new Paragraph("File: " + fileVenta).setFontSize(10));

        // Celda 3: Forma de Pago - todo en una línea
        Cell formaPagoCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

        String formaPago = documento.getFormaPagoDescripcion() != null
                ? documento.getFormaPagoDescripcion().toUpperCase()
                : "NO ESPECIFICADO";
        
        System.out.println("🔍 DEBUG PDF - FormaPagoId: " + documento.getFormaPagoId());
        System.out.println("🔍 DEBUG PDF - FormaPagoDescripcion: " + documento.getFormaPagoDescripcion());

        formaPagoCell.add(new Paragraph("Forma de Pago: " + formaPago).setFontSize(10));

        // Agregar las celdas a la tabla
        resumenTable.addCell(monedaCell);
        resumenTable.addCell(fileCell);
        resumenTable.addCell(formaPagoCell);

        document.add(resumenTable);
        document.add(new Paragraph("").setMarginTop(2));
    }

    // 3. TABLA DE DETALLES DE SERVICIOS
    private void addServicesTable(Document document, DocumentoCobranzaResponseDTO documento) {
        // CONFIGURAR ANCHOS DE COLUMNAS DE PRODUCTOS (en puntos absolutos sobre 523
        // disponibles) - Cant, Código, Descripción, P.U., Total
        float[] productColumnWidths = { 40f, 60f, 280f, 70f, 73f }; // Total = 523 puntos

        Table servicesTable = new Table(UnitValue.createPointArray(productColumnWidths));
        servicesTable.setWidth(523f);

        servicesTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        // Encabezados de tabla con fondo gris (anchos controlados por array)
        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Código").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("P.U.").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        BigDecimal subTotalAmount = BigDecimal.ZERO;
        // Agregar filas de detalles
        for (DetalleDocumentoCobranzaResponseDTO detalle : documento.getDetalles()) {
            
            String cantidad = String.valueOf(detalle.getCantidad() != null ? detalle.getCantidad() : 1);
            servicesTable.addCell(new Cell().add(new Paragraph(cantidad).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));
 
            String codigoProducto = detalle.getProductoDescripcion() != null ? detalle.getProductoDescripcion() : "N/A";
            servicesTable.addCell(new Cell().add(new Paragraph(codigoProducto).setFontSize(8))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)
                    .setKeepTogether(true));

            Paragraph descripcionParagraph = new Paragraph(
                    detalle.getDescripcion() != null ? detalle.getDescripcion() : "")
                    .setFontSize(9)
                    .setFixedLeading(11);

            servicesTable.addCell(new Cell()
                    .add(descripcionParagraph)
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setMaxWidth(280f)
                    .setPadding(3));

            String precioUnitario = String.format("$ %.2f",
                    detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO);
            servicesTable.addCell(new Cell().add(new Paragraph(precioUnitario).setFontSize(8))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT)
                    .setKeepTogether(true));

            // Calcular total del detalle: cantidad * precio
            BigDecimal cantidadBD = detalle.getCantidad() != null ? new BigDecimal(detalle.getCantidad())
                    : BigDecimal.ONE;
            BigDecimal precioBD = detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO;
            BigDecimal totalDetalle = cantidadBD.multiply(precioBD);

            String total = String.format("%.2f", totalDetalle);
            servicesTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

            subTotalAmount = subTotalAmount.add(totalDetalle);
        }
        document.add(servicesTable);

        float[] totalsColumnWidths = { 40f, 60f, 260f, 90f, 73f }; // Configuracion del ancho de las columnas del cuadro
                                                                   // de totales

        Table totalsTable = new Table(UnitValue.createPointArray(totalsColumnWidths));
        servicesTable.setWidth(523f);

        totalsTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        BigDecimal CostoEnvio = documento.getCostoEnvio() != null ? documento.getCostoEnvio() : BigDecimal.ZERO;
        BigDecimal totalAmount = subTotalAmount.add(CostoEnvio);
        String totalEnLetras = convertirNumeroALetras(totalAmount.doubleValue(), documento.getMoneda());

        totalsTable.addCell(new Cell(3, 1));
        totalsTable.addCell(new Cell(1, 2).add(new Paragraph("Son " + totalEnLetras).setFontSize(9)))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);

        Cell subtotalLabelCell = new Cell()
                .add(new Paragraph("Subtotal").setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        totalsTable.addCell(subtotalLabelCell);

        String subtotal = String.format("%.2f", subTotalAmount);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + subtotal).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

        // Celda con información de "Creado por:" con nombre del usuario autenticado
        // (ocupa 2 filas y 2 columnas)
        String nombreUsuario = getAuthenticatedUserName();
        totalsTable.addCell(new Cell(2, 2)
                .add(new Paragraph("Creado por: " + nombreUsuario).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5));

        totalsTable.addCell(new Cell().add(new Paragraph("COSTO DE ENVIO").setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        String costoEnvio = String.format("%.2f",
                documento.getCostoEnvio() != null ? documento.getCostoEnvio() : BigDecimal.ZERO);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + costoEnvio).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("PRECIO VENTA").setBold().setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER));

        String total = String.format("%.2f", totalAmount);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

        document.add(totalsTable);
        document.add(new Paragraph("\n"));
    }

    // 4. CUADRO DE OBSERVACIONES
    private void addObservationsBox(Document document, DocumentoCobranzaResponseDTO documento) {
        document.add(new Paragraph("\n"));

        float[] observationColumnWidths = { 523f };
        Table observationsTable = new Table(UnitValue.createPointArray(observationColumnWidths));
        observationsTable.setWidth(523f);
        observationsTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        String observaciones = documento.getObservaciones() != null ? documento.getObservaciones() : "";

        Paragraph labelParagraph = new Paragraph()
                .add(new com.itextpdf.layout.element.Text("OBSERVACIONES: ").setBold()).setFontSize(10);
        Paragraph observationsParagraph = new Paragraph(observaciones).setFontSize(10).setFixedLeading(11);

        observationsTable.addCell(new Cell()
                .add(labelParagraph)
                .add(observationsParagraph)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setMaxWidth(523f)
                .setPadding(3));

        document.add(observationsTable);
    }

    // 5. PIE DE PÁGINA EN CADA HOJA
    private void addPageFooter(PdfDocument pdfDocument) {
        // Crear el event handler para agregar pie de página en cada página
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new com.itextpdf.kernel.events.IEventHandler() {
            @Override
            public void handleEvent(Event event) {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfPage page = docEvent.getPage();

                try {
                    PdfCanvas canvas = new PdfCanvas(page); // Posición del pie de página
                    float x = (page.getPageSize().getLeft() + page.getPageSize().getRight()) / 2;
                    float y = page.getPageSize().getBottom() + 20;

                    canvas.beginText() // Agregar el texto del pie de página centrado
                            .setFontAndSize(PdfFontFactory.createFont(), 8)
                            .moveText(x - 80, y) // Centrar aproximadamente
                            .showText("Representación Impresa de DOCUMENTO DE COBRANZA")
                            .endText();
                } catch (Exception e) {// Manejo silencioso de errores para no interrumpir la generación
                    throw new RuntimeException("Error al agregar el pie de página en el PDF del documento de cobranza");
                }
            }
        });
    }

    // ✅ NUEVO: Agregar texto rojo centrado SOLO en la última página
    private void addRedTextToLastPage(Document document, PdfDocument pdfDocument) {
        try {
            int lastPageNumber = pdfDocument.getNumberOfPages();

            // Crear párrafo con el texto en ROJO, MAYÚSCULAS y CENTRADO
            Paragraph redText = new Paragraph("NO VÁLIDO PARA CRÉDITO FISCAL, SOLO PARA FINES DE COBRANZA")
                    .setFontColor(ColorConstants.RED)
                    .setFontSize(10)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFixedPosition(
                            lastPageNumber, // Número de página (la última)
                            40, // Margen izquierdo (x)
                            60, // Posición vertical desde abajo (y)
                            515 // Ancho del texto (ancho de página - márgenes)
                    );

            document.add(redText);

        } catch (Exception e) {
            // Si falla agregar el texto rojo, no detener la generación del PDF
            System.err.println("Advertencia: No se pudo agregar el texto rojo al PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<DocumentoCobranzaResponseDTO> mapToResponseList(List<DocumentoCobranza> documentos) {
        return documentos.stream().map(documentoCobranzaMapper::toResponseDTO).toList();
    }

    /**
     * Obtiene el nombre del usuario autenticado actualmente
     * 
     * @return Nombre del usuario autenticado o "Usuario desconocido" si no hay
     *         autenticación
     */
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

    private void crearDetallesDesdeCotizacion(DocumentoCobranza documentoCobranza, Integer cotizacionId) {
        // Obtener todos los detalles de la cotización
        List<DetalleCotizacionResponseDto> detallesCotizacion = detalleCotizacionService
                .findByCotizacionId(cotizacionId);

        // Filtrar solo los detalles seleccionados
        List<DetalleCotizacionResponseDto> detallesSeleccionados = detallesCotizacion.stream()
                .filter(detalle -> detalle.getSeleccionado() != null && detalle.getSeleccionado())
                .toList();

        // Por cada detalle seleccionado, crear N detalles de documento de cobranza (donde N = cantidad)
        for (DetalleCotizacionResponseDto detalleCot : detallesSeleccionados) {
            int cantidad = detalleCot.getCantidad() != null ? detalleCot.getCantidad() : 1;

            // Crear un detalle de documento de cobranza por cada unidad de cantidad
            for (int i = 0; i < cantidad; i++) {
                DetalleDocumentoCobranza detalleDoc = new DetalleDocumentoCobranza();

                // Asignar el documento de cobranza
                detalleDoc.setDocumentoCobranza(documentoCobranza);

                // Mapear datos desde el detalle de cotización
                detalleDoc.setCantidad(1); // Cada registro individual tiene cantidad = 1
                detalleDoc.setDescripcion(detalleCot.getDescripcion());
                detalleDoc.setPrecio(
                        detalleCot.getPrecioHistorico() != null ? detalleCot.getPrecioHistorico() : BigDecimal.ZERO);

                // Asignar producto si existe
                if (detalleCot.getProducto() != null) {
                    Producto producto = new Producto();
                    producto.setId(detalleCot.getProducto().getId());
                    detalleDoc.setProducto(producto);
                }

                // Guardar el detalle
                detalleDocumentoCobranzaRepository.save(detalleDoc);
            }
        }
    }
}