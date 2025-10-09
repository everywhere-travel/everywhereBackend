package com.everywhere.backend.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.DocumentoCobranzaPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentoCobranzaPdfServiceImpl implements DocumentoCobranzaPdfService {

    @Autowired
    private DocumentoCobranzaService documentoCobranzaService;

    @Override
    public ByteArrayInputStream generatePdf(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        DocumentoCobranzaDTO documento = documentoCobranzaService.generateDocumentoCobranza(cotizacionId, nroSerie,
                fileVenta, costoEnvio);
        
        return generatePdfFromDTO(documento);
    }
    
    @Override
    public ByteArrayInputStream generatePdfFromDTO(DocumentoCobranzaDTO documento) {
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

            // 2. INFORMACIÓN DEL CLIENTE Y DOCUMENTO
            addClientAndDocumentInfo(pdfDoc, documento);

            // 3. TABLA DE DETALLES DE SERVICIOS
            if (documento.getDetalles() != null && !documento.getDetalles().isEmpty()) {
                addServicesTable(pdfDoc, documento);
            }

            // 4. SECCIÓN DE TOTALES
            addTotalsSection(pdfDoc, documento);

            // 5. PIE DE PÁGINA
            addFooter(pdfDoc, documento);

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
        
        // Columna izquierda - Logo y datos de empresa
        Cell leftCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setWidth(UnitValue.createPercentValue(60));
                
        // Logo y nombre de empresa (simulado con texto)
        leftCell.add(new Paragraph("everywhere")
                .setFontSize(24)
                .setBold()
                .setFontColor(ColorConstants.BLUE));
        leftCell.add(new Paragraph("TRAVEL")
                .setFontSize(12)
                .setMarginTop(-5));
                
        leftCell.add(new Paragraph("EVERYWHERE TRAVEL SAC")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10));
                
        leftCell.add(new Paragraph("MZ.J LTE.10 URB.SOLILUZ, TRUJILLO, PERU")
                .setFontSize(10));
                
        leftCell.add(new Paragraph("Teléfono: 044 729-728")
                .setFontSize(10));
                
        leftCell.add(new Paragraph("Celular: +51 944 493 851 / 947 755 582")
                .setFontSize(10));
        
        // Columna derecha - RUC y tipo de documento
        Cell rightCell = new Cell()
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setWidth(UnitValue.createPercentValue(40))
                .setTextAlignment(TextAlignment.CENTER);
                
        rightCell.add(new Paragraph("R.U.C. Nº 20602292941")
                .setFontSize(12)
                .setBold());
                
        rightCell.add(new Paragraph("DOCUMENTO DE COBRANZA")
                .setFontSize(12)
                .setBold()
                .setMarginTop(5));
                
        // Agregar número de documento si se tiene
        if (documento.getNroSerie() != null) {
            rightCell.add(new Paragraph(documento.getNroSerie())
                    .setFontSize(12)
                    .setBold()
                    .setMarginTop(5));
        }
        
        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        
        document.add(headerTable);
        document.add(new Paragraph("\n").setMarginTop(10));
    }

    // 2. INFORMACIÓN DEL CLIENTE Y DOCUMENTO
    private void addClientAndDocumentInfo(Document document, DocumentoCobranzaDTO documento) {
        // Tabla con bordes para información del documento
        Table infoTable = new Table(4).setWidth(UnitValue.createPercentValue(100));
        infoTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
        
        // Primera fila
        infoTable.addCell(new Cell().add(new Paragraph("Fecha de emisión:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        
        String fechaEmision = documento.getFechaEmision() != null ? 
            documento.getFechaEmision().format(DateTimeFormatter.ofPattern("d/MM/yyyy")) :
            LocalDate.now().format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
        
        infoTable.addCell(new Cell().add(new Paragraph(fechaEmision).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        infoTable.addCell(new Cell().add(new Paragraph("Fecha de vencimiento:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        infoTable.addCell(new Cell().add(new Paragraph(fechaEmision).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        
        // Segunda fila - Cliente
        infoTable.addCell(new Cell().add(new Paragraph("Señor(es):").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        String nombreCliente = documento.getClienteEmail() != null ? 
            documento.getClienteEmail().toUpperCase() : "CLIENTE";
            
        infoTable.addCell(new Cell(1, 3).add(new Paragraph(nombreCliente).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        
        // Tercera fila - Documento
        infoTable.addCell(new Cell().add(new Paragraph("Documento - DNI:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        String numeroDocumento = documento.getClienteTelefono() != null ? 
            documento.getClienteTelefono() : "00000000";
            
        infoTable.addCell(new Cell().add(new Paragraph(numeroDocumento).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        infoTable.addCell(new Cell().add(new Paragraph("Dirección:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        String direccion = documento.getClienteDireccion() != null ? 
            documento.getClienteDireccion() : "";
            
        infoTable.addCell(new Cell().add(new Paragraph(direccion).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
        
        // Tabla de resumen (Moneda, Cuotas, Forma de Pago)
        Table resumenTable = new Table(6).setWidth(UnitValue.createPercentValue(100));
        resumenTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
        
        resumenTable.addCell(new Cell().add(new Paragraph("Moneda:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        String moneda = documento.getMoneda() != null ? 
            documento.getMoneda() + " - Dólar Americano" : "USD - Dólar Americano";
            
        resumenTable.addCell(new Cell().add(new Paragraph(moneda).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        resumenTable.addCell(new Cell().add(new Paragraph("Cuotas:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        resumenTable.addCell(new Cell().add(new Paragraph("0").setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        resumenTable.addCell(new Cell().add(new Paragraph("Forma de Pago:").setBold().setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                
        String formaPago = documento.getFormaPago() != null ? 
            documento.getFormaPago().toUpperCase() : "DEPOSITO";
            
        resumenTable.addCell(new Cell().add(new Paragraph(formaPago).setFontSize(10))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        
        document.add(resumenTable);
        document.add(new Paragraph("\n"));
    }

    // 3. TABLA DE DETALLES DE SERVICIOS
    private void addServicesTable(Document document, DocumentoCobranzaDTO documento) {
        // Crear tabla de servicios con el formato exacto
        Table servicesTable = new Table(6).setWidth(UnitValue.createPercentValue(100));
        servicesTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

        // Encabezados de tabla con fondo gris
        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER));
                
        servicesTable.addHeaderCell(new Cell().add(new Paragraph("Unidad").setBold().setFontSize(10))
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
            // Cantidad - primera fila vacía, segunda con valor
            servicesTable.addCell(new Cell().add(new Paragraph(""))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            servicesTable.addCell(new Cell().add(new Paragraph("ZZ").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph("TKT").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph(detalle.getDescripcion() != null ? detalle.getDescripcion() : "").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            servicesTable.addCell(new Cell().add(new Paragraph("$").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph("").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            
            // Segunda fila con cantidad y precios
            String cantidad = String.valueOf(detalle.getCantidad() != null ? detalle.getCantidad() : 1);
            servicesTable.addCell(new Cell().add(new Paragraph(cantidad).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph("ZZ").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph("TKT").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.CENTER));
            servicesTable.addCell(new Cell().add(new Paragraph("").setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            
            String precioUnitario = String.format("%.2f", detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
            servicesTable.addCell(new Cell().add(new Paragraph(precioUnitario).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.RIGHT));
            
            String total = String.format("%.2f", detalle.getSubtotalDetalle() != null ? detalle.getSubtotalDetalle() : BigDecimal.ZERO);
            servicesTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(servicesTable);
        document.add(new Paragraph("\n"));
    }

    // 4. RESUMEN Y TOTALES
    private void addTotalsSection(Document document, DocumentoCobranzaDTO documento) {
        // Crear tabla de totales con formato exacto del documento
        Table totalsTable = new Table(new float[]{3, 1}).setWidth(UnitValue.createPercentValue(50))
                .setMarginLeft(300);
        
        // Agregar filas de totales con el formato exacto
        totalsTable.addCell(new Cell().add(new Paragraph("Tarifa:").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        String subtotal = String.format("%.2f", documento.getSubtotal() != null ? documento.getSubtotal() : BigDecimal.ZERO);
        totalsTable.addCell(new Cell().add(new Paragraph("$ " + subtotal).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("I.G.V.:").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        // Calcular IGV (18% del subtotal)
        BigDecimal igvCalculado = documento.getSubtotal() != null ? 
            documento.getSubtotal().multiply(new BigDecimal("0.18")) : BigDecimal.ZERO;
        String igv = String.format("%.2f", igvCalculado);
        totalsTable.addCell(new Cell().add(new Paragraph("$ " + igv).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("Otros Impuestos:").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        totalsTable.addCell(new Cell().add(new Paragraph("$ 0.00").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("I.S.C.:").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        totalsTable.addCell(new Cell().add(new Paragraph("$ 0.00").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        totalsTable.addCell(new Cell().add(new Paragraph("OTROS CARGOS:").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        String otrosCargos = String.format("%.2f", documento.getCostoEnvio() != null ? documento.getCostoEnvio() : BigDecimal.ZERO);
        totalsTable.addCell(new Cell().add(new Paragraph("$ " + otrosCargos).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        // Línea separadora
        totalsTable.addCell(new Cell(1, 2).add(new Paragraph("").setFontSize(5))
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER));

        // Total final en negrita
        totalsTable.addCell(new Cell().add(new Paragraph("PRECIO VENTA:").setBold().setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT));
        String total = String.format("%.2f", documento.getTotal() != null ? documento.getTotal() : BigDecimal.ZERO);
        totalsTable.addCell(new Cell().add(new Paragraph("$ " + total).setBold().setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(totalsTable);
        document.add(new Paragraph("\n"));
    }

    // 5. PIE DE PÁGINA
    private void addFooter(Document document, DocumentoCobranzaDTO documento) {
        // Espacio antes del footer
        document.add(new Paragraph("\n"));
        
        // Crear tabla para el footer con el formato del documento real
        Table footerTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
        
        // Sección "CREADO POR"
        Cell createdByCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        
        String createdBy = "CREADO POR: SISTEMA";
        
        // Agregar fecha actual
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaCreacion = now.format(formatter);
        
        createdByCell.add(new Paragraph(createdBy).setFontSize(9).setBold());
        createdByCell.add(new Paragraph("Fecha: " + fechaCreacion).setFontSize(9));
        
        footerTable.addCell(createdByCell);
        document.add(footerTable);
        
        // Observaciones si existen
        if (documento.getObservaciones() != null && !documento.getObservaciones().trim().isEmpty()) {
            document.add(new Paragraph("\n"));
            Paragraph obsTitle = new Paragraph("OBSERVACIONES:")
                    .setBold()
                    .setFontSize(10)
                    .setMarginBottom(5);
            document.add(obsTitle);
            
            Paragraph obsContent = new Paragraph(documento.getObservaciones())
                    .setFontSize(9)
                    .setMarginBottom(10);
            document.add(obsContent);
        }
    }
}