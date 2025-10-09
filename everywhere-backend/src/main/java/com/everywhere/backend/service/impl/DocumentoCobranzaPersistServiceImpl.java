package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaPersistService;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentoCobranzaPersistServiceImpl implements DocumentoCobranzaPersistService {

    @Autowired
    private DocumentoCobranzaRepository documentoCobranzaRepository;

    @Autowired
    private DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;

    @Autowired
    private CotizacionService cotizacionService;

    @Override
    public DocumentoCobranza saveDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        
        // Obtener datos de la cotización
        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);
        
        if (cotizacion == null) {
            throw new RuntimeException("Cotización no encontrada con ID: " + cotizacionId);
        }

        // Crear documento de cobranza
        DocumentoCobranza documento = new DocumentoCobranza();
        
        // Generar número secuencial
        documento.setNumero(generateNextDocumentNumber());
        documento.setFechaEmision(LocalDateTime.now());
        documento.setObservaciones(cotizacion.getObservacion());
        
        // Asignar relaciones desde la cotización
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
        
        // Campos transient para el PDF
        documento.setNroSerie(nroSerie);
        documento.setFileVenta(fileVenta);
        documento.setCostoEnvio(costoEnvio);
        documento.setMoneda(cotizacion.getMoneda());
        documento.setCotizacionId((long) cotizacionId);

        // Guardar documento principal
        documento = documentoCobranzaRepository.save(documento);

        // Crear y guardar detalles
        List<DetalleDocumentoCobranza> detalles = new ArrayList<>();
        
        if (cotizacion.getDetalles() != null) {
            for (DetalleCotizacionSimpleDTO detalleCot : cotizacion.getDetalles()) {
                if (detalleCot.getSeleccionado() != null && detalleCot.getSeleccionado()) {
                    
                    DetalleDocumentoCobranza detalle = new DetalleDocumentoCobranza();
                    detalle.setDocumentoCobranza(documento);
                    detalle.setCantidad(detalleCot.getCantidad() != null ? detalleCot.getCantidad() : 0);
                    detalle.setDescripcion(detalleCot.getDescripcion());
                    detalle.setFechaCreacion(LocalDateTime.now());
                    
                    // Precio unitario (precio histórico de la cotización)
                    BigDecimal precioUnitario = detalleCot.getPrecioHistorico() != null ? 
                        detalleCot.getPrecioHistorico() : BigDecimal.ZERO;
                    detalle.setPrecio(precioUnitario);
                    
                    // Asignar producto si existe
                    if (detalleCot.getProducto() != null) {
                        Producto producto = new Producto();
                        producto.setId(detalleCot.getProducto().getId());
                        detalle.setProducto(producto);
                    }
                    
                    // Campos transient para cálculos
                    detalle.setComision(detalleCot.getComision() != null ? detalleCot.getComision() : BigDecimal.ZERO);
                    detalle.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad())));
                    
                    detalles.add(detalle);
                }
            }
        }
        
        // Guardar detalles
        detalleDocumentoCobranzaRepository.saveAll(detalles);
        documento.setDetalles(detalles);
        
        return documento;
    }

    @Override
    public String generateNextDocumentNumber() {
        Optional<String> lastNumberOpt = documentoCobranzaRepository.findLastDocumentNumber();
        
        if (lastNumberOpt.isPresent()) {
            String lastNumber = lastNumberOpt.get();
            // Formato: DC01-000000300
            String[] parts = lastNumber.split("-");
            if (parts.length == 2) {
                int nextNumber = Integer.parseInt(parts[1]) + 1;
                return String.format("DC01-%09d", nextNumber);
            }
        }
        
        // Si no hay documentos previos, empezar con DC01-000000300
        return "DC01-000000300";
    }

    @Override
    public DocumentoCobranza findById(Long id) {
        return documentoCobranzaRepository.findById(id).orElse(null);
    }

    @Override
    public DocumentoCobranza findByNumero(String numero) {
        return documentoCobranzaRepository.findByNumero(numero).orElse(null);
    }

    @Override
    public List<DocumentoCobranza> findAll() {
        return documentoCobranzaRepository.findAll();
    }
}