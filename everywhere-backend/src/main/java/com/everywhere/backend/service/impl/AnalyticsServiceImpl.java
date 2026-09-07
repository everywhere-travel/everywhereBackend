package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.analytics.*;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.repository.ReciboRepository;
import com.everywhere.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private DocumentoCobranzaRepository documentoCobranzaRepository;

    @Autowired
    private ReciboRepository reciboRepository;
    
    @Autowired
    private PersonaRepository personaRepository;
    
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;
    
    @Autowired
    private PersonaJuridicaRepository personaJuridicaRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate) {
        
        List<DocumentoCobranza> documentos = documentoCobranzaRepository.findByFechaEmisionBetween(startDate, endDate);
        List<Recibo> recibos = reciboRepository.findByFechaEmisionBetween(startDate, endDate);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal comisionesTotales = BigDecimal.ZERO; 
        int ventasCerradas = documentos.size(); 

        Map<String, BigDecimal> ventasPorVendedor = new HashMap<>();
        Map<String, Integer> cotizacionesPorVendedor = new HashMap<>();
        
        Map<String, TopProductDTO> productosMap = new HashMap<>();
        Map<String, BigDecimal> ventasPorFecha = new TreeMap<>();
        
        Map<String, TopClientDTO> clientsMap = new HashMap<>();
        Map<String, TopDestinationDTO> destinationsMap = new HashMap<>();
        Map<String, Integer> demographyMap = new HashMap<>();

        // Pre-fetch Personas para evitar N+1
        List<Integer> personaIds = new ArrayList<>();
        for (DocumentoCobranza doc : documentos) {
            if (doc.getCotizacion() != null && doc.getCotizacion().getPersonas() != null) {
                personaIds.add(doc.getCotizacion().getPersonas().getId());
            }
        }
        for (Recibo rec : recibos) {
            if (rec.getCotizacion() != null && rec.getCotizacion().getPersonas() != null) {
                personaIds.add(rec.getCotizacion().getPersonas().getId());
            }
        }
        
        Map<Integer, PersonaNatural> naturalesMap = new HashMap<>();
        Map<Integer, PersonaJuridica> juridicasMap = new HashMap<>();
        if (!personaIds.isEmpty()) {
            List<PersonaNatural> pnList = personaNaturalRepository.findByPersonasIdIn(personaIds);
            for (PersonaNatural pn : pnList) {
                naturalesMap.put(pn.getPersonas().getId(), pn);
            }
            List<PersonaJuridica> pjList = personaJuridicaRepository.findByPersonasIdIn(personaIds);
            for (PersonaJuridica pj : pjList) {
                juridicasMap.put(pj.getPersonas().getId(), pj);
            }
        }

        // Process Documentos de Cobranza
        Set<Long> processedCotizacionIds = new HashSet<>();
        
        for (DocumentoCobranza doc : documentos) {
            BigDecimal docTotal = BigDecimal.ZERO;
            if (doc.getDetalles() != null) {
                for (DetalleDocumentoCobranza det : doc.getDetalles()) {
                    if (det.getPrecio() != null && det.getCantidad() != null) {
                        BigDecimal lineTotal = det.getPrecio().multiply(BigDecimal.valueOf(det.getCantidad()));
                        docTotal = docTotal.add(lineTotal);
                        
                        if (det.getProducto() != null) {
                            String prodName = det.getProducto().getDescripcion();
                            TopProductDTO p = productosMap.getOrDefault(prodName, new TopProductDTO(prodName, 0, BigDecimal.ZERO));
                            p.setCantidadVendida(p.getCantidadVendida() + det.getCantidad());
                            p.setIngresoGenerado(p.getIngresoGenerado().add(lineTotal));
                            productosMap.put(prodName, p);
                        }
                    }
                }
            }
            totalVentas = totalVentas.add(docTotal);
            
            String vendorName = (doc.getUsuario() != null && doc.getUsuario().getNombre() != null) 
                                ? doc.getUsuario().getNombre() : "Sin Asignar";
                                
            ventasPorVendedor.put(vendorName, ventasPorVendedor.getOrDefault(vendorName, BigDecimal.ZERO).add(docTotal));
            cotizacionesPorVendedor.put(vendorName, cotizacionesPorVendedor.getOrDefault(vendorName, 0) + 1);
            
            String dateStr = doc.getFechaEmision() != null ? doc.getFechaEmision().format(DateTimeFormatter.ISO_DATE) : "";
            if (!dateStr.isEmpty()) {
                ventasPorFecha.put(dateStr, ventasPorFecha.getOrDefault(dateStr, BigDecimal.ZERO).add(docTotal));
            }

            if (doc.getCotizacion() != null) {
                processedCotizacionIds.add((long) doc.getCotizacion().getId());
                
                // Destination
                String dest = doc.getCotizacion().getOrigenDestino();
                if (dest != null && !dest.trim().isEmpty()) {
                    TopDestinationDTO td = destinationsMap.getOrDefault(dest, new TopDestinationDTO(dest, 0, BigDecimal.ZERO));
                    td.setCantidadViajes(td.getCantidadViajes() + 1);
                    td.setIngresoGenerado(td.getIngresoGenerado().add(docTotal));
                    destinationsMap.put(dest, td);
                }
                
                // Client and Demography
                if (doc.getCotizacion().getPersonas() != null) {
                    Integer pid = doc.getCotizacion().getPersonas().getId();
                    String clientName = "Cliente " + pid;
                    String nacio = "No Especificado";
                    String resi = "No Especificado";
                    
                    if (naturalesMap.containsKey(pid)) {
                        PersonaNatural pn = naturalesMap.get(pid);
                        clientName = (pn.getNombres() != null ? pn.getNombres() : "") + " " + 
                                     (pn.getApellidosPaterno() != null ? pn.getApellidosPaterno() : "");
                        
                        if (pn.getViajero() != null) {
                            if (pn.getViajero().getNacionalidad() != null && !pn.getViajero().getNacionalidad().trim().isEmpty()) {
                                nacio = pn.getViajero().getNacionalidad().trim().toUpperCase();
                            }
                            if (pn.getViajero().getResidencia() != null && !pn.getViajero().getResidencia().trim().isEmpty()) {
                                resi = pn.getViajero().getResidencia().trim().toUpperCase();
                            }
                        }
                    } else if (juridicasMap.containsKey(pid)) {
                        PersonaJuridica pj = juridicasMap.get(pid);
                        if (pj.getRazonSocial() != null && !pj.getRazonSocial().trim().isEmpty()) {
                            clientName = pj.getRazonSocial();
                        }
                    }
                    
                    clientName = clientName.trim();
                    if (clientName.isEmpty()) clientName = "Desconocido";
                    
                    TopClientDTO tc = clientsMap.getOrDefault(clientName, new TopClientDTO(clientName, BigDecimal.ZERO, 0));
                    tc.setMontoComprado(tc.getMontoComprado().add(docTotal));
                    tc.setFrecuenciaCompra(tc.getFrecuenciaCompra() + 1);
                    clientsMap.put(clientName, tc);
                    
                    String demoKey = nacio + "|" + resi;
                    demographyMap.put(demoKey, demographyMap.getOrDefault(demoKey, 0) + 1);
                }
            }
        }

        // Process Recibos (Pagos del momento)
        for (Recibo rec : recibos) {
            boolean hasDocCobranza = rec.getDocumentoCobranza() != null || 
                                     (rec.getCotizacion() != null && processedCotizacionIds.contains((long) rec.getCotizacion().getId()));
            
            if (!hasDocCobranza) {
                BigDecimal recTotal = BigDecimal.ZERO;
                if (rec.getDetalleRecibo() != null) {
                    for (DetalleRecibo det : rec.getDetalleRecibo()) {
                        if (det.getPrecio() != null && det.getCantidad() != null) {
                            BigDecimal lineTotal = det.getPrecio().multiply(BigDecimal.valueOf(det.getCantidad()));
                            recTotal = recTotal.add(lineTotal);
                            
                            if (det.getProducto() != null) {
                                String prodName = det.getProducto().getDescripcion();
                                TopProductDTO p = productosMap.getOrDefault(prodName, new TopProductDTO(prodName, 0, BigDecimal.ZERO));
                                p.setCantidadVendida(p.getCantidadVendida() + det.getCantidad());
                                p.setIngresoGenerado(p.getIngresoGenerado().add(lineTotal));
                                productosMap.put(prodName, p);
                            }
                        }
                    }
                }
                totalVentas = totalVentas.add(recTotal);
                
                String vendorName = (rec.getUsuario() != null && rec.getUsuario().getNombre() != null) 
                                    ? rec.getUsuario().getNombre() : "Sin Asignar";
                                    
                ventasPorVendedor.put(vendorName, ventasPorVendedor.getOrDefault(vendorName, BigDecimal.ZERO).add(recTotal));
                
                String dateStr = rec.getFechaEmision() != null ? rec.getFechaEmision().format(DateTimeFormatter.ISO_DATE) : "";
                if (!dateStr.isEmpty()) {
                    ventasPorFecha.put(dateStr, ventasPorFecha.getOrDefault(dateStr, BigDecimal.ZERO).add(recTotal));
                }
                
                if (rec.getCotizacion() != null) {
                    String dest = rec.getCotizacion().getOrigenDestino();
                    if (dest != null && !dest.trim().isEmpty()) {
                        TopDestinationDTO td = destinationsMap.getOrDefault(dest, new TopDestinationDTO(dest, 0, BigDecimal.ZERO));
                        td.setCantidadViajes(td.getCantidadViajes() + 1);
                        td.setIngresoGenerado(td.getIngresoGenerado().add(recTotal));
                        destinationsMap.put(dest, td);
                    }
                    
                    if (rec.getCotizacion().getPersonas() != null) {
                        Integer pid = rec.getCotizacion().getPersonas().getId();
                        String clientName = "Cliente " + pid;
                        String nacio = "No Especificado";
                        String resi = "No Especificado";
                        
                        if (naturalesMap.containsKey(pid)) {
                            PersonaNatural pn = naturalesMap.get(pid);
                            clientName = (pn.getNombres() != null ? pn.getNombres() : "") + " " + 
                                         (pn.getApellidosPaterno() != null ? pn.getApellidosPaterno() : "");
                            
                            if (pn.getViajero() != null) {
                                if (pn.getViajero().getNacionalidad() != null && !pn.getViajero().getNacionalidad().trim().isEmpty()) {
                                    nacio = pn.getViajero().getNacionalidad().trim().toUpperCase();
                                }
                                if (pn.getViajero().getResidencia() != null && !pn.getViajero().getResidencia().trim().isEmpty()) {
                                    resi = pn.getViajero().getResidencia().trim().toUpperCase();
                                }
                            }
                        } else if (juridicasMap.containsKey(pid)) {
                            PersonaJuridica pj = juridicasMap.get(pid);
                            if (pj.getRazonSocial() != null && !pj.getRazonSocial().trim().isEmpty()) {
                                clientName = pj.getRazonSocial();
                            }
                        }
                        
                        clientName = clientName.trim();
                        if (clientName.isEmpty()) clientName = "Desconocido";
                        
                        TopClientDTO tc = clientsMap.getOrDefault(clientName, new TopClientDTO(clientName, BigDecimal.ZERO, 0));
                        tc.setMontoComprado(tc.getMontoComprado().add(recTotal));
                        tc.setFrecuenciaCompra(tc.getFrecuenciaCompra() + 1);
                        clientsMap.put(clientName, tc);
                        
                        String demoKey = nacio + "|" + resi;
                        demographyMap.put(demoKey, demographyMap.getOrDefault(demoKey, 0) + 1);
                    }
                }
            }
        }

        KpiSummaryDTO kpi = new KpiSummaryDTO(totalVentas, comisionesTotales, ventasCerradas);
        
        List<TopVendorDTO> topVendors = ventasPorVendedor.entrySet().stream()
                .map(e -> new TopVendorDTO(e.getKey(), e.getValue(), cotizacionesPorVendedor.getOrDefault(e.getKey(), 0)))
                .sorted((a, b) -> b.getMontoVendido().compareTo(a.getMontoVendido()))
                .limit(5)
                .collect(Collectors.toList());
                
        List<TopProductDTO> topProducts = productosMap.values().stream()
                .sorted((a, b) -> b.getCantidadVendida().compareTo(a.getCantidadVendida()))
                .limit(5)
                .collect(Collectors.toList());
                
        List<SalesChartDataDTO> salesChart = ventasPorFecha.entrySet().stream()
                .map(e -> new SalesChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
                
        List<TopClientDTO> topClients = clientsMap.values().stream()
                .sorted((a, b) -> b.getMontoComprado().compareTo(a.getMontoComprado()))
                .limit(10)
                .collect(Collectors.toList());
                
        List<TopDestinationDTO> topDestinations = destinationsMap.values().stream()
                .sorted((a, b) -> b.getCantidadViajes().compareTo(a.getCantidadViajes()))
                .limit(10)
                .collect(Collectors.toList());
                
        List<DemographyDTO> clientDemographics = demographyMap.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("\\|");
                    return new DemographyDTO(parts[0], parts[1], e.getValue());
                })
                .sorted((a, b) -> b.getCantidadClientes().compareTo(a.getCantidadClientes()))
                .collect(Collectors.toList());

        // Process New Clients created in this period
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Personas> nuevosClientes = personaRepository.findByCreadoBetween(startDateTime, endDateTime);
        
        Map<String, Integer> clientesNuevosPorFecha = new TreeMap<>();
        for (Personas p : nuevosClientes) {
            if (p.getCreado() != null) {
                String dateStr = p.getCreado().format(DateTimeFormatter.ISO_LOCAL_DATE);
                clientesNuevosPorFecha.put(dateStr, clientesNuevosPorFecha.getOrDefault(dateStr, 0) + 1);
            }
        }
        
        List<NewClientsChartDataDTO> newClientsChart = clientesNuevosPorFecha.entrySet().stream()
                .map(e -> new NewClientsChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new AnalyticsDashboardDTO(kpi, topVendors, topProducts, salesChart, topClients, clientDemographics, topDestinations, newClientsChart);
    }
}
