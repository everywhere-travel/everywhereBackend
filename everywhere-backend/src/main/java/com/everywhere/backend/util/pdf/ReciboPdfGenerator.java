package com.everywhere.backend.util.pdf;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboResponseDTO;

/**
 * Generador de PDF para Recibo - Extiende PdfGenerator
 */
@Component
public class ReciboPdfGenerator extends PdfGenerator<ReciboResponseDTO, DetalleReciboResponseDTO> {

    public ReciboPdfGenerator(NumberToTextConverter numberToTextConverter) {
        super(numberToTextConverter);
    }

    @Override
    protected String getDocumentTitle() {
        return "RECIBO";
    }

    @Override
    protected String getFooterText() {
        return "Representación Impresa de RECIBO";
    }

    @Override
    protected List<DetalleReciboResponseDTO> getDetalles(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getDetalles();
    }

    @Override
    protected String getNumeroDocumento(ReciboResponseDTO documentoDTO) {
        // Concatenar serie y correlativo para formar el número completo (ej:
        // R01-000000001)
        if (documentoDTO.getSerie() != null && documentoDTO.getCorrelativo() != null) {
            return String.format("%s-%09d", documentoDTO.getSerie(), documentoDTO.getCorrelativo());
        }
        return null;
    }

    @Override
    protected String getFechaEmision(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getFechaEmision() != null
                ? documentoDTO.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : null;
    }

    @Override
    protected String getClienteNombre(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getClienteNombre();
    }

    @Override
    protected String getClienteDocumento(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getClienteDocumento();
    }

    @Override
    protected String getTipoDocumentoCliente(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getTipoDocumentoCliente();
    }

    @Override
    protected String getSucursalDescripcion(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getSucursalDescripcion();
    }

    @Override
    protected String getMoneda(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getMoneda();
    }

    @Override
    protected String getFileVenta(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getFileVenta();
    }

    @Override
    protected String getFormaPagoDescripcion(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getFormaPagoDescripcion();
    }

    @Override
    protected String getObservaciones(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getObservaciones();
    }

    @Override
    protected BigDecimal getCostoEnvio(ReciboResponseDTO documentoDTO) {
        // Recibo no tiene costo de envío, retorna 0
        return BigDecimal.ZERO;
    }

    @Override
    protected boolean showCostoEnvio() {
        // Recibo no muestra la fila de costo de envío
        return false;
    }

    @Override
    protected boolean showDisclaimer() {
        // Recibo no muestra el disclaimer de crédito fiscal
        return false;
    }

    @Override
    protected String getFechaVencimiento(ReciboResponseDTO documentoDTO) {
        return documentoDTO.getFechaVencimiento() != null
                ? documentoDTO.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : null;
    }

    @Override
    protected Integer getCantidad(DetalleReciboResponseDTO detalle) {
        return detalle.getCantidad();
    }

    @Override
    protected String getProductoDescripcion(DetalleReciboResponseDTO detalle) {
        return detalle.getProductoDescripcion();
    }

    @Override
    protected String getDescripcionDetalle(DetalleReciboResponseDTO detalle) {
        return detalle.getDescripcion();
    }

    @Override
    protected BigDecimal getPrecio(DetalleReciboResponseDTO detalle) {
        return detalle.getPrecio();
    }

    @Override
    protected Long getDetalleId(DetalleReciboResponseDTO detalle) {
        return detalle.getId();
    }

    @Override
    protected void addObservationsBox(com.itextpdf.layout.Document document, ReciboResponseDTO documentoDTO) {
        // Primero agregamos el cuadro de observaciones por defecto
        super.addObservationsBox(document, documentoDTO);

        // Luego agregamos el saldo debajo, suelto como texto
        document.add(new com.itextpdf.layout.element.Paragraph("\n"));

        java.math.BigDecimal saldo = documentoDTO.getSaldoPendienteActual() != null ? documentoDTO.getSaldoPendienteActual() : java.math.BigDecimal.ZERO;
        String moneda = documentoDTO.getMoneda() != null && documentoDTO.getMoneda().equals("USD") ? "$ " : "S/ ";
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00", new java.text.DecimalFormatSymbols(java.util.Locale.US));

        String textoSaldo = "SALDO PENDIENTE A PAGAR: " + moneda + formatter.format(saldo);

        com.itextpdf.layout.element.Paragraph paragraph = new com.itextpdf.layout.element.Paragraph(textoSaldo)
                .setFontSize(10)
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT);

        document.add(paragraph);
    }
}
