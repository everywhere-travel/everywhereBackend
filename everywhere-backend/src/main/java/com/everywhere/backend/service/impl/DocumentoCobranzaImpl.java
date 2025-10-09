package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionSimpleDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.DocumentoCobranzaPersistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentoCobranzaImpl implements DocumentoCobranzaService {

    @Autowired
    private CotizacionService cotizacionService;
    
    @Autowired
    private DocumentoCobranzaPersistService documentoCobranzaPersistService;

    @Override
    public DocumentoCobranzaDTO generateDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        
        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);
        
        if (cotizacion == null) {
            throw new RuntimeException("Cotización no encontrada con ID: " + cotizacionId);
        }

        DocumentoCobranzaDTO documento = new DocumentoCobranzaDTO();
        
        // Campos manuales
        documento.setNroSerie(nroSerie);
        documento.setFileVenta(fileVenta);
        documento.setCostoEnvio(costoEnvio != null ? BigDecimal.valueOf(costoEnvio) : BigDecimal.ZERO);
        
        // Campos de cotización
        documento.setFechaEmision(cotizacion.getFechaVencimiento());
        documento.setMoneda(cotizacion.getMoneda());
        documento.setObservaciones(cotizacion.getObservacion());
        
        // Cliente
        if (cotizacion.getPersonas() != null) {
            documento.setClienteEmail(cotizacion.getPersonas().getEmail());
            documento.setClienteTelefono(cotizacion.getPersonas().getTelefono());
            documento.setClienteDireccion(cotizacion.getPersonas().getDireccion());
        }
        
        // Punto de compra (sucursal)
        if (cotizacion.getSucursal() != null) {
            documento.setPuntoCompra(cotizacion.getSucursal().getDescripcion() + 
                (cotizacion.getSucursal().getDireccion() != null ? " - " + cotizacion.getSucursal().getDireccion() : ""));
        }
        
        // Forma de pago
        if (cotizacion.getFormaPago() != null) {
            documento.setFormaPago(cotizacion.getFormaPago().getDescripcion());
        }
        
        // Procesar detalles (solo seleccionados)
        List<DocumentoCobranzaDTO.DetalleDocumentoCobranza> detallesDoc = new ArrayList<>();
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        
        if (cotizacion.getDetalles() != null) {
            for (DetalleCotizacionSimpleDTO detalle : cotizacion.getDetalles()) {
                if (detalle.getSeleccionado() != null && detalle.getSeleccionado()) {
                    DocumentoCobranzaDTO.DetalleDocumentoCobranza detalleDoc = new DocumentoCobranzaDTO.DetalleDocumentoCobranza();
                    
                    detalleDoc.setCantidad(detalle.getCantidad() != null ? detalle.getCantidad() : 0);
                    detalleDoc.setDescripcion(detalle.getDescripcion());
                    
                    // Código producto
                    if (detalle.getProducto() != null) {
                        detalleDoc.setCodigoProducto(detalle.getProducto().getCodigo());
                    }
                    
                    BigDecimal precioUnitario = detalle.getPrecioHistorico() != null ? detalle.getPrecioHistorico() : BigDecimal.ZERO;
                    BigDecimal comision = detalle.getComision() != null ? detalle.getComision() : BigDecimal.ZERO;
                    
                    detalleDoc.setPrecioUnitario(precioUnitario);
                    detalleDoc.setComision(comision);
                    
                    // Calcular subtotal del detalle (cantidad * precio + comisión)
                    BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleDoc.getCantidad())).add(comision);
                    detalleDoc.setSubtotalDetalle(subtotalDetalle);
                    
                    subtotalGeneral = subtotalGeneral.add(subtotalDetalle);
                    detallesDoc.add(detalleDoc);
                }
            }
        }
        
        documento.setDetalles(detallesDoc);
        documento.setSubtotal(subtotalGeneral);
        
        // Total = subtotal + costo de envío
        BigDecimal total = subtotalGeneral.add(documento.getCostoEnvio());
        documento.setTotal(total);
        
        // Importe en letras
        documento.setImporteEnLetras(convertirNumeroALetras(total.doubleValue(), documento.getMoneda()));
        
        return documento;
    }
    
    @Override
    public DocumentoCobranzaDTO generateAndSaveDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        // Guardar en base de datos
        DocumentoCobranza documentoEntity = documentoCobranzaPersistService.saveDocumentoCobranza(cotizacionId, nroSerie, fileVenta, costoEnvio);
        
        // Convertir a DTO
        return convertToDTO(documentoEntity);
    }
    
    @Override
    public DocumentoCobranzaDTO convertToDTO(DocumentoCobranza entity) {
        DocumentoCobranzaDTO dto = new DocumentoCobranzaDTO();
        
        // Campos básicos
        dto.setNroSerie(entity.getNroSerie());
        dto.setFileVenta(entity.getFileVenta());
        dto.setCostoEnvio(entity.getCostoEnvio() != null ? BigDecimal.valueOf(entity.getCostoEnvio()) : BigDecimal.ZERO);
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setMoneda(entity.getMoneda());
        dto.setObservaciones(entity.getObservaciones());
        
        // Datos del cliente
        if (entity.getPersona() != null) {
            dto.setClienteEmail(entity.getPersona().getEmail());
            dto.setClienteTelefono(entity.getPersona().getTelefono());
            dto.setClienteDireccion(entity.getPersona().getDireccion());
        }
        
        // Punto de compra
        if (entity.getSucursal() != null) {
            dto.setPuntoCompra(entity.getSucursal().getDescripcion());
        }
        
        // Forma de pago
        if (entity.getFormaPago() != null) {
            dto.setFormaPago(entity.getFormaPago().getDescripcion());
        }
        
        // Convertir detalles
        List<DocumentoCobranzaDTO.DetalleDocumentoCobranza> detallesDto = new ArrayList<>();
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        
        if (entity.getDetalles() != null) {
            for (DetalleDocumentoCobranza detalleEntity : entity.getDetalles()) {
                DocumentoCobranzaDTO.DetalleDocumentoCobranza detalleDto = new DocumentoCobranzaDTO.DetalleDocumentoCobranza();
                
                detalleDto.setCantidad(detalleEntity.getCantidad());
                detalleDto.setDescripcion(detalleEntity.getDescripcion());
                detalleDto.setPrecioUnitario(detalleEntity.getPrecio());
                
                // Código del producto
                if (detalleEntity.getProducto() != null) {
                    detalleDto.setCodigoProducto(detalleEntity.getProducto().getCodigo());
                }
                
                // Usar campos transient para cálculos
                detalleDto.setComision(detalleEntity.getComision() != null ? detalleEntity.getComision() : BigDecimal.ZERO);
                
                // Calcular subtotal del detalle
                BigDecimal subtotalDetalle = detalleEntity.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleEntity.getCantidad()))
                    .add(detalleDto.getComision());
                detalleDto.setSubtotalDetalle(subtotalDetalle);
                
                subtotalGeneral = subtotalGeneral.add(subtotalDetalle);
                detallesDto.add(detalleDto);
            }
        }
        
        dto.setDetalles(detallesDto);
        dto.setSubtotal(subtotalGeneral);
        
        // Total = subtotal + costo de envío
        BigDecimal total = subtotalGeneral.add(dto.getCostoEnvio());
        dto.setTotal(total);
        
        // Importe en letras
        dto.setImporteEnLetras(convertirNumeroALetras(total.doubleValue(), entity.getMoneda()));
        
        return dto;
    }
    
    private String convertirNumeroALetras(double numero, String moneda) {
        // Implementación básica - se puede mejorar con una librería especializada
        if (numero == 0) {
            return "CERO " + (moneda != null ? moneda.toUpperCase() : "");
        }
        
        // Por simplicidad, solo formato básico
        return String.format("%.2f %s", numero, moneda != null ? moneda.toUpperCase() : "").toUpperCase();
    }
}
