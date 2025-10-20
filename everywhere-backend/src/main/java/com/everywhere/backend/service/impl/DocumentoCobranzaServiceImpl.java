package com.everywhere.backend.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
import com.everywhere.backend.model.dto.DetalleCotizacionSimpleDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentoCobranzaServiceImpl implements DocumentoCobranzaService {

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private DocumentoCobranzaRepository documentoCobranzaRepository;

    @Autowired
    private DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;

    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;

    @Autowired
    private PersonaJuridicaRepository personaJuridicaRepository;

    // ========== MÉTODOS PÚBLICOS DE LA INTERFAZ ==========

    @Override
    public DocumentoCobranzaResponseDTO createDocumentoCobranza(Integer cotizacionId, String fileVenta,
            Double costoEnvio) {
        DocumentoCobranza existente = documentoCobranzaRepository.findByCotizacionId(cotizacionId).orElse(null);
        if (existente != null) {
            throw new RuntimeException("Ya existe un documento de cobranza para la cotización ID: " + cotizacionId);
        }

        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);
        if (cotizacion == null) {
            throw new RuntimeException("Cotización no encontrada con ID: " + cotizacionId);
        }

        DocumentoCobranza documento = new DocumentoCobranza();
        documento.setNumero(generateNextDocumentNumber());
        documento.setFechaEmision(LocalDateTime.now());
        documento.setObservaciones(cotizacion.getObservacion());

        com.everywhere.backend.model.entity.Cotizacion cotizacionEntity = new com.everywhere.backend.model.entity.Cotizacion();
        cotizacionEntity.setId(cotizacionId);
        documento.setCotizacion(cotizacionEntity);

        if (cotizacion.getPersonas() != null) {
            Personas persona = new Personas();
            persona.setId(cotizacion.getPersonas().getId());
            documento.setPersona(persona);
        }

        if (cotizacion.getSucursal() != null) {
            Sucursal sucursal = new Sucursal();
            sucursal.setId(cotizacion.getSucursal().getId());
            documento.setSucursal(sucursal);
        }

        if (cotizacion.getFormaPago() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(cotizacion.getFormaPago().getId());
            documento.setFormaPago(formaPago);
        }

        documento.setNroSerie(generateNextSerieNumber());
        documento.setFileVenta(fileVenta);
        documento.setCostoEnvio(costoEnvio);
        documento.setMoneda(cotizacion.getMoneda());

        documento = documentoCobranzaRepository.save(documento);

        List<DetalleDocumentoCobranza> detalles = new ArrayList<>();

        if (cotizacion.getDetalles() != null) {
            for (DetalleCotizacionSimpleDTO detalleCot : cotizacion.getDetalles()) {
                if (detalleCot.getSeleccionado() != null && detalleCot.getSeleccionado()) {
                    DetalleDocumentoCobranza detalle = new DetalleDocumentoCobranza();
                    detalle.setDocumentoCobranza(documento);
                    detalle.setCantidad(detalleCot.getCantidad() != null ? detalleCot.getCantidad() : 0);
                    detalle.setDescripcion(detalleCot.getDescripcion());
                    detalle.setFechaCreacion(LocalDateTime.now());

                    BigDecimal precioUnitario = detalleCot.getPrecioHistorico() != null
                            ? detalleCot.getPrecioHistorico()
                            : BigDecimal.ZERO;
                    detalle.setPrecio(precioUnitario);

                    if (detalleCot.getProducto() != null) {
                        Producto producto = new Producto();
                        producto.setId(detalleCot.getProducto().getId());
                        detalle.setProducto(producto);
                    }

                    detalles.add(detalle);
                }
            }
        }

        if (!detalles.isEmpty()) {
            detalleDocumentoCobranzaRepository.saveAll(detalles);
            documento.setDetalles(detalles);
        }

        return convertToResponseDTO(documento);
    }

    @Override
    public ByteArrayInputStream generatePdf(Long documentoId) {
        DocumentoCobranza documento = findDocumentoById(documentoId);
        if (documento == null) {
            throw new RuntimeException("Documento de cobranza no encontrado con ID: " + documentoId);
        }

        DocumentoCobranzaDTO documentoDTO = convertToDTO(documento);
        return generatePdfFromDTO(documentoDTO);
    }

    @Override
    public DocumentoCobranzaDTO convertToDTO(DocumentoCobranza entity) {
        DocumentoCobranzaDTO dto = new DocumentoCobranzaDTO();

        dto.setNroSerie(entity.getNroSerie());
        dto.setFileVenta(entity.getFileVenta());
        dto.setCostoEnvio(
                entity.getCostoEnvio() != null ? BigDecimal.valueOf(entity.getCostoEnvio()) : BigDecimal.ZERO);
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setMoneda(entity.getMoneda());
        dto.setObservaciones(entity.getObservaciones());

        if (entity.getPersona() != null) {
            dto.setClienteEmail(entity.getPersona().getEmail());
            dto.setClienteTelefono(entity.getPersona().getTelefono());
            
            // Buscar información adicional del cliente (nombre y documento)
            String nombreCompleto;
            String documento;
            
            // Buscar en PersonaNatural primero
            Optional<PersonaNatural> personaNatural = personaNaturalRepository.findByPersonasId(entity.getPersona().getId());
            if (personaNatural.isPresent()) {
                PersonaNatural pn = personaNatural.get();
                nombreCompleto = (pn.getNombres() + " " + pn.getApellidos()).trim();
                documento = pn.getDocumento() != null ? pn.getDocumento() : "00000000";
            } else {
                // Si no es persona natural, buscar en PersonaJuridica
                Optional<PersonaJuridica> personaJuridica = personaJuridicaRepository.findByPersonasId(entity.getPersona().getId());
                if (personaJuridica.isPresent()) {
                    PersonaJuridica pj = personaJuridica.get();
                    nombreCompleto = pj.getRazonSocial() != null ? pj.getRazonSocial() : "EMPRESA";
                    documento = pj.getRuc() != null ? pj.getRuc() : "00000000000";
                } else {
                    // Valores por defecto si no se encuentra en ninguna tabla
                    nombreCompleto = "CLIENTE";
                    documento = "00000000";
                }
            }
            
            dto.setClienteNombre(nombreCompleto);
            dto.setClienteDocumento(documento);
        }

        if (entity.getSucursal() != null) {
            dto.setSucursalDescripcion(entity.getSucursal().getDescripcion());
            dto.setPuntoCompra(entity.getSucursal().getDescripcion());
        }

        if (entity.getFormaPago() != null) {
            dto.setFormaPago(entity.getFormaPago().getDescripcion());
        }

        List<DocumentoCobranzaDTO.DetalleDocumentoCobranza> detallesDto = new ArrayList<>();
        BigDecimal subtotalGeneral = BigDecimal.ZERO;

        if (entity.getDetalles() != null) {
            for (DetalleDocumentoCobranza detalleEntity : entity.getDetalles()) {
                DocumentoCobranzaDTO.DetalleDocumentoCobranza detalleDto = new DocumentoCobranzaDTO.DetalleDocumentoCobranza();

                detalleDto.setCantidad(detalleEntity.getCantidad());
                detalleDto.setDescripcion(detalleEntity.getDescripcion());
                detalleDto.setPrecioUnitario(detalleEntity.getPrecio());

                if (detalleEntity.getProducto() != null) {
                    detalleDto.setCodigoProducto(detalleEntity.getProducto().getCodigo());
                }

                // Calcular subtotal del detalle: cantidad * precio
                BigDecimal cantidad = detalleEntity.getCantidad() != null ? new BigDecimal(detalleEntity.getCantidad())
                        : BigDecimal.ONE;
                BigDecimal precio = detalleEntity.getPrecio() != null ? detalleEntity.getPrecio() : BigDecimal.ZERO;
                BigDecimal subtotalDetalle = cantidad.multiply(precio);

                subtotalGeneral = subtotalGeneral.add(subtotalDetalle);
                detallesDto.add(detalleDto);
            }
        }

        dto.setDetalles(detallesDto);
        dto.setSubtotal(subtotalGeneral);

        BigDecimal total = subtotalGeneral.add(dto.getCostoEnvio());
        dto.setTotal(total);

        dto.setImporteEnLetras(convertirNumeroALetras(total.doubleValue(), entity.getMoneda()));

        return dto;
    }

    @Override
    public DocumentoCobranzaResponseDTO convertToResponseDTO(DocumentoCobranza entity) {
        DocumentoCobranzaResponseDTO dto = new DocumentoCobranzaResponseDTO();

        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setObservaciones(entity.getObservaciones());
        dto.setNroSerie(entity.getNroSerie());
        dto.setFileVenta(entity.getFileVenta());
        dto.setCostoEnvio(entity.getCostoEnvio());
        dto.setMoneda(entity.getMoneda());

        if (entity.getCotizacion() != null) {
            dto.setCotizacionId(entity.getCotizacion().getId());
        }

        if (entity.getPersona() != null) {
            dto.setPersonaId(entity.getPersona().getId());
            dto.setClienteEmail(entity.getPersona().getEmail());
        }

        if (entity.getSucursal() != null) {
            dto.setSucursalId(entity.getSucursal().getId());
            dto.setSucursalDescripcion(entity.getSucursal().getDescripcion());
        }

        if (entity.getFormaPago() != null) {
            dto.setFormaPagoId(entity.getFormaPago().getId());
            dto.setFormaPagoDescripcion(entity.getFormaPago().getDescripcion());
        }

        return dto;
    }

    @Override
    public DocumentoCobranzaResponseDTO findById(Long id) {
        DocumentoCobranza entity = documentoCobranzaRepository.findById(id).orElse(null);
        return entity != null ? convertToResponseDTO(entity) : null;
    }

    @Override
    public DocumentoCobranzaResponseDTO findByNumero(String numero) {
        DocumentoCobranza entity = documentoCobranzaRepository.findByNumero(numero).orElse(null);
        return entity != null ? convertToResponseDTO(entity) : null;
    }

    @Override
    public List<DocumentoCobranzaResponseDTO> findAll() {
        return documentoCobranzaRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public DocumentoCobranzaResponseDTO findByCotizacionId(Integer cotizacionId) {
        DocumentoCobranza entity = documentoCobranzaRepository.findByCotizacionId(cotizacionId).orElse(null);
        return entity != null ? convertToResponseDTO(entity) : null;
    }

    // ========== MÉTODOS PRIVADOS ==========

    private DocumentoCobranza findDocumentoById(Long id) {
        return documentoCobranzaRepository.findById(id).orElse(null);
    }

    private String generateNextDocumentNumber() {
        Optional<String> lastNumberOpt = documentoCobranzaRepository.findLastDocumentNumber();

        if (lastNumberOpt.isPresent()) {
            String lastNumber = lastNumberOpt.get();
            String[] parts = lastNumber.split("-");
            if (parts.length == 2) {
                String serie = parts[0];
                int numero = Integer.parseInt(parts[1]);
                int nextNumero = numero + 1;

                if (nextNumero > 999999999) {
                    return "DC02-000000001";
                }

                return String.format("%s-%09d", serie, nextNumero);
            }
        }

        return "DC01-000000300";
    }

    private String generateNextSerieNumber() {
        return "DC-" + System.currentTimeMillis();
    }

    private String convertirNumeroALetras(double numero, String moneda) {
        if (numero == 0) {
            return "Cero " + obtenerNombreMoneda(moneda);
        }

        // Separar parte entera y decimales
        long parteEntera = (long) numero;
        int decimales = (int) Math.round((numero - parteEntera) * 100);

        String parteEnteraEnLetras = convertirEnteroALetras(parteEntera);
        String nombreMoneda = obtenerNombreMoneda(moneda);

        // Convertir todo a minúsculas y capitalizar solo la primera letra
        String resultado;
        if (decimales == 0) {
            resultado = parteEnteraEnLetras + " con 00/100 " + nombreMoneda;
        } else {
            resultado = parteEnteraEnLetras + " con " + String.format("%02d", decimales) + "/100 " + nombreMoneda;
        }

        // Convertir a minúsculas y capitalizar solo la primera letra
        resultado = resultado.toLowerCase();
        if (resultado.length() > 0) {
            resultado = Character.toUpperCase(resultado.charAt(0)) + resultado.substring(1);
        }

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
            if (millones == 1) {
                resultado += "UN MILLÓN ";
            } else {
                resultado += convertirGrupoTresCifras((int) millones) + " MILLONES ";
            }
            numero %= 1000000;
        }

        // Miles
        if (numero >= 1000) {
            long miles = numero / 1000;
            if (miles == 1) {
                resultado += "MIL ";
            } else {
                resultado += convertirGrupoTresCifras((int) miles) + " MIL ";
            }
            numero %= 1000;
        }

        // Centenas, decenas y unidades
        if (numero > 0) {
            resultado += convertirGrupoTresCifras((int) numero);
        }

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
            if (numero == 100) {
                resultado += "CIEN";
            } else {
                resultado += centenas[c] + " ";
            }
        }

        // Decenas y unidades
        int resto = numero % 100;
        if (resto >= 10 && resto <= 19) {
            resultado += especiales[resto - 10];
        } else {
            int d = resto / 10;
            int u = resto % 10;

            if (d == 2 && u > 0) {
                resultado += "VEINTI" + unidades[u].toLowerCase();
            } else {
                if (d > 0) {
                    resultado += decenas[d];
                    if (u > 0) {
                        resultado += " Y " + unidades[u];
                    }
                } else if (u > 0) {
                    resultado += unidades[u];
                }
            }
        }

        return resultado.trim();
    }

    private ByteArrayInputStream generatePdfFromDTO(DocumentoCobranzaDTO documento) {
        if (documento == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean hasValidData = false;

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document pdfDoc = new Document(pdfDocument);

            // 1. ENCABEZADO DE LA EMPRESA
            addCompanyHeader(pdfDoc, documento);
            hasValidData = true;

            // 2. TABLA DE RESUMEN (MONEDA, FILE, FORMA DE PAGO)
            addSummaryTable(pdfDoc, documento);

            // 3. TABLA DE DETALLES DE SERVICIOS
            if (documento.getDetalles() != null && !documento.getDetalles().isEmpty()) {
                addServicesTable(pdfDoc, documento);
            }

            // 4. CUADRO DE OBSERVACIONES (separado)
            addObservationsBox(pdfDoc, documento);

            // 5. PIE DE PÁGINA EN CADA HOJA
            addPageFooter(pdfDocument);

            pdfDoc.close();

            if (!hasValidData) {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del documento de cobranza", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // 1. ENCABEZADO DE LA EMPRESA
    private void addCompanyHeader(Document document, DocumentoCobranzaDTO documento) {
        // Crear tabla principal para el encabezado
        Table headerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

        // Columna izquierda - Logo y datos de empresa en celdas separadas
        Cell leftCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setWidth(UnitValue.createPercentValue(60));

        // 1. Celda del Logo/Imagen
        Table logoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        Cell logoCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(10)
                .setHeight(60);

        // Cargar y agregar la imagen del logo
        try {
            // Intentar cargar desde el classpath primero
            String logoPath = "/logo/everyLogo.png";
            java.io.InputStream logoStream = getClass().getResourceAsStream(logoPath);

            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                Image logoImage = new Image(ImageDataFactory.create(logoBytes));

                // Poner un tamaño fijo para que no interfiera con el layout, alineada a la
                // izquierda
                logoImage.setWidth(150); // Ancho fijo en puntos
                logoImage.setHeight(50); // Alto fijo en puntos

                logoCell.add(logoImage);
                logoStream.close();
            } else {
                throw new Exception("No se encontró el archivo en el classpath");
            }
        } catch (Exception e) {
            try {
                // Intentar con ruta absoluta como segunda opción
                String logoPath = "src/main/resources/logo/everyLogo.png";
                Image logoImage = new Image(ImageDataFactory.create(logoPath));

                // Poner un tamaño fijo para que no interfiera con el layout
                logoImage.setWidth(220); // Ancho fijo en puntos
                logoImage.setHeight(50); // Alto fijo en puntos

                logoCell.add(logoImage);
            } catch (Exception e2) {
                // Si no se puede cargar la imagen, usar texto como fallback
                System.out.println("No se pudo cargar la imagen del logo: " + e2.getMessage());
                logoCell.add(new Paragraph("everywhere")
                        .setFontSize(24)
                        .setBold()
                        .setFontColor(ColorConstants.BLUE));
                logoCell.add(new Paragraph("TRAVEL")
                        .setFontSize(12)
                        .setMarginTop(-5));
            }
        }

        logoTable.addCell(logoCell);
        leftCell.add(logoTable);

        // Espacio entre celdas
        leftCell.add(new Paragraph("\n").setMarginTop(7));

        // 2. Celda única con información de la empresa (3 renglones)
        Table companyInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        Cell companyInfoCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(8);

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
        Cell rightCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setWidth(UnitValue.createPercentValue(40));

        // Cuadro del RUC y DOCUMENTO DE COBRANZA
        Table rucTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

        Cell rucCell = new Cell()
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);

        rucCell.add(new Paragraph("R.U.C. Nº 20602292941")
                .setFontSize(10)
                .setBold());

        rucCell.add(new Paragraph("DOCUMENTO DE COBRANZA")
                .setFontSize(14)
                .setBold()
                .setMarginTop(5));

        // Agregar número de documento si se tiene
        if (documento.getNroSerie() != null) {
            rucCell.add(new Paragraph(documento.getNroSerie())
                    .setFontSize(12)
                    .setBold()
                    .setMarginTop(5));
        }

        rucTable.addCell(rucCell);
        rightCell.add(rucTable);

        // Espacio reducido entre cuadros (75% menos que original)
        rightCell.add(new Paragraph("\n").setMarginTop(1));

        // Cuadro de información del cliente
        Table clientInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

        Cell clientCell = new Cell()
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setPadding(8);

        String fechaEmision = documento.getFechaEmision() != null
                ? documento.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String nombreCliente = documento.getClienteNombre() != null ? documento.getClienteNombre().toUpperCase()
                : "CLIENTE";

        String numeroDocumento = documento.getClienteDocumento() != null ? documento.getClienteDocumento() : "00000000";

        String sucursalDescripcion = documento.getSucursalDescripcion() != null ? documento.getSucursalDescripcion()
                : "";

        // Crear una tabla interna de 4 filas y 2 columnas para organizar cada dato en
        // su propia fila
        Table innerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

        // Fila 1 - Solo Fecha de emisión
        innerTable.addCell(new Cell().add(new Paragraph("Fecha de emisión:").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(fechaEmision).setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));

        // Fila 2 - Solo Señor(es)
        innerTable.addCell(new Cell().add(new Paragraph("Señor(es):").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(nombreCliente).setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));

        // Fila 3 - Solo Documento
        innerTable.addCell(new Cell().add(new Paragraph("Documento - DNI:").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(numeroDocumento).setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));

        // Fila 4 - Solo Sucursal
        innerTable.addCell(new Cell().add(new Paragraph("Sucursal:").setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));
        innerTable.addCell(new Cell().add(new Paragraph(sucursalDescripcion).setFontSize(9))
                .setBorder(Border.NO_BORDER).setPadding(1));

        clientCell.add(innerTable);
        clientInfoTable.addCell(clientCell);
        rightCell.add(clientInfoTable);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("").setMarginTop(1));
    }

    // 2. TABLA DE RESUMEN (MONEDA, FILE, FORMA DE PAGO)
    private void addSummaryTable(Document document, DocumentoCobranzaDTO documento) {
        // Tabla de resumen con 3 columnas en una sola fila sin líneas divisorias
        Table resumenTable = new Table(3).setWidth(UnitValue.createPercentValue(100));
        resumenTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        // Celda 1: Moneda - todo en una línea
        Cell monedaCell = new Cell()
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);

        String moneda = documento.getMoneda() != null ? documento.getMoneda() + " - Dólar Americano"
                : "USD - Dólar Americano";

        monedaCell.add(new Paragraph("Moneda: " + moneda).setFontSize(10));

        // Celda 2: File - todo en una línea
        Cell fileCell = new Cell()
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);

        String fileVenta = documento.getFileVenta() != null ? documento.getFileVenta() : "N/A";

        fileCell.add(new Paragraph("File: " + fileVenta).setFontSize(10));

        // Celda 3: Forma de Pago - todo en una línea
        Cell formaPagoCell = new Cell()
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);

        String formaPago = documento.getFormaPago() != null ? documento.getFormaPago().toUpperCase() : "DEPOSITO";

        formaPagoCell.add(new Paragraph("Forma de Pago: " + formaPago).setFontSize(10));

        // Agregar las celdas a la tabla
        resumenTable.addCell(monedaCell);
        resumenTable.addCell(fileCell);
        resumenTable.addCell(formaPagoCell);

        document.add(resumenTable);
        document.add(new Paragraph("").setMarginTop(2));
    }

    // 3. TABLA DE DETALLES DE SERVICIOS
    private void addServicesTable(Document document, DocumentoCobranzaDTO documento) {
        // CONFIGURAR ANCHOS DE COLUMNAS DE PRODUCTOS (en puntos absolutos sobre 523
        // disponibles)
        // Cant, Código, Descripción, P.U., Total
        float[] productColumnWidths = { 40f, 60f, 280f, 70f, 73f }; // Total = 523 puntos

        Table servicesTable = new Table(UnitValue.createPointArray(productColumnWidths));
        servicesTable.setWidth(523f);

        servicesTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        // Encabezados de tabla con fondo gris (anchos controlados por array)
        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Código").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("P.U.").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        // Agregar filas de detalles
        for (DocumentoCobranzaDTO.DetalleDocumentoCobranza detalle : documento.getDetalles()) {
            String cantidad = String.valueOf(detalle.getCantidad() != null ? detalle.getCantidad() : 1);
            servicesTable.addCell(new Cell().add(new Paragraph(cantidad).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));

            servicesTable.addCell(new Cell().add(new Paragraph("TKT").setFontSize(8))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setKeepTogether(true));

            servicesTable.addCell(new Cell()
                    .add(new Paragraph(detalle.getDescripcion() != null ? detalle.getDescripcion() : "").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));

            String precioUnitario = String.format("$ %.2f",
                    detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
            servicesTable.addCell(new Cell().add(new Paragraph(precioUnitario).setFontSize(8))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setKeepTogether(true));

            // Calcular total del detalle: cantidad * precio
            BigDecimal cantidadBD = detalle.getCantidad() != null ? new BigDecimal(detalle.getCantidad())
                    : BigDecimal.ONE;
            BigDecimal precioBD = detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal totalDetalle = cantidadBD.multiply(precioBD);

            String total = String.format("%.2f", totalDetalle);
            servicesTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.RIGHT));
        }
        document.add(servicesTable);

        // CONFIGURAR ANCHOS DE COLUMNAS DE TOTALES (ajustar aquí)
        // Las 5 columnas originales para mantener la estructura exacta
        float[] totalsColumnWidths = { 40f, 60f, 260f, 90f, 73f }; // Porcentajes que suman 100%

        Table totalsTable = new Table(UnitValue.createPointArray(totalsColumnWidths));
        servicesTable.setWidth(523f);

        totalsTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        BigDecimal totalAmount = documento.getTotal() != null ? documento.getTotal() : BigDecimal.ZERO;
        String totalEnLetras = convertirNumeroALetras(totalAmount.doubleValue(), documento.getMoneda());

        totalsTable.addCell(new Cell(3, 1));
        totalsTable.addCell(new Cell(1, 2).add(new Paragraph("Son " + totalEnLetras).setFontSize(9)))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);

        Cell subtotalLabelCell = new Cell()
                .add(new Paragraph("Subtotal").setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);
        totalsTable.addCell(subtotalLabelCell);

        String subtotal = String.format("%.2f",
                documento.getSubtotal() != null ? documento.getSubtotal() : BigDecimal.ZERO);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + subtotal).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell(2, 2));

        totalsTable.addCell(new Cell().add(new Paragraph("COSTO DE ENVIO").setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        String costoEnvio = String.format("%.2f",
                documento.getCostoEnvio() != null ? documento.getCostoEnvio() : BigDecimal.ZERO);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + costoEnvio).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("PRECIO VENTA").setBold().setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));

        String total = String.format("%.2f", documento.getTotal() != null ? documento.getTotal() : BigDecimal.ZERO);

        totalsTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(totalsTable);
        document.add(new Paragraph("\n"));
    }

    // 4. CUADRO DE OBSERVACIONES
    private void addObservationsBox(Document document, DocumentoCobranzaDTO documento) {
        if (documento.getObservaciones() != null && !documento.getObservaciones().trim().isEmpty()) {
            // Espacio antes del cuadro
            document.add(new Paragraph("\n"));

            // Crear tabla de una sola celda para el cuadro de observaciones
            Table observationsTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

            Cell observationsCell = new Cell()
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

            // Contenido de observaciones
            Paragraph observationsParagraph = new Paragraph()
                    .add(new com.itextpdf.layout.element.Text("OBSERVACIONES: ").setBold())
                    .add(new com.itextpdf.layout.element.Text(documento.getObservaciones()))
                    .setFontSize(10)
                    .setMarginBottom(0);

            observationsCell.add(observationsParagraph);

            observationsTable.addCell(observationsCell);
            document.add(observationsTable);
        }
    }

    // 5. PIE DE PÁGINA EN CADA HOJA
    private void addPageFooter(PdfDocument pdfDocument) {
        // Crear el event handler para agregar pie de página en cada página
        pdfDocument.addEventHandler(com.itextpdf.kernel.events.PdfDocumentEvent.END_PAGE,
                new com.itextpdf.kernel.events.IEventHandler() {
                    @Override
                    public void handleEvent(com.itextpdf.kernel.events.Event event) {
                        com.itextpdf.kernel.events.PdfDocumentEvent docEvent = (com.itextpdf.kernel.events.PdfDocumentEvent) event;
                        com.itextpdf.kernel.pdf.PdfPage page = docEvent.getPage();

                        try {
                            com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas = new com.itextpdf.kernel.pdf.canvas.PdfCanvas(
                                    page);

                            // Posición del pie de página (parte inferior de la página)
                            float x = (page.getPageSize().getLeft() + page.getPageSize().getRight()) / 2;
                            float y = page.getPageSize().getBottom() + 20;

                            // Agregar el texto del pie de página centrado
                            canvas.beginText()
                                    .setFontAndSize(com.itextpdf.kernel.font.PdfFontFactory.createFont(), 8)
                                    .moveText(x - 80, y) // Centrar aproximadamente
                                    .showText("Representación Impresa de DOCUMENTO DE COBRANZA")
                                    .endText();
                        } catch (Exception e) {
                            // Manejo silencioso de errores para no interrumpir la generación
                            System.err.println("Error al agregar pie de página: " + e.getMessage());
                        }
                    }
                });
    }

    @Override
    public DocumentoCobranzaResponseDTO updateDocumento(Long id, DocumentoCobranzaUpdateDTO updateDTO) {
        DocumentoCobranza documento = documentoCobranzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id));

        // Actualizar solo los campos que vienen en el DTO (no nulos)
        if (updateDTO.getFileVenta() != null) {
            documento.setFileVenta(updateDTO.getFileVenta());
        }
        
        if (updateDTO.getCostoEnvio() != null) {
            documento.setCostoEnvio(updateDTO.getCostoEnvio());
        }
        
        if (updateDTO.getObservaciones() != null) {
            documento.setObservaciones(updateDTO.getObservaciones());
        }

        DocumentoCobranza updated = documentoCobranzaRepository.save(documento);
        return convertToResponseDTO(updated);
    }
}