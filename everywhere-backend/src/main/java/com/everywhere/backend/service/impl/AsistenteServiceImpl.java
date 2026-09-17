package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.chat.ChatMessageRequestDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageResponseDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.service.AsistenteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsistenteServiceImpl implements AsistenteService {

    private final CotizacionRepository cotizacionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent}")
    private String geminiApiUrl;

    @Override
    public ChatMessageResponseDTO procesarMensaje(ChatMessageRequestDTO request) {
        String mensajeUsuario = request.getMessage() != null ? request.getMessage().trim() : "";

        // 1. Recopilar datos reales de cotizaciones (ordenados)
        List<Cotizacion> todas = cotizacionRepository.findAll();
        String contextoPendientes = recopilarContextoPendientes(todas);

        // 2. Procesar con Gemini API si la API Key está presente
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                String respuestaIA = llamarGeminiAPI(mensajeUsuario, contextoPendientes);
                if (respuestaIA != null && !respuestaIA.isBlank()) {
                    return ChatMessageResponseDTO.builder()
                            .reply(respuestaIA)
                            .suggestions(generarSugerencias())
                            .timestamp(LocalDateTime.now())
                            .build();
                }
            } catch (Exception e) {
                log.error("Error al comunicarse con Gemini API, usando fallback local: {}", e.getMessage());
            }
        }

        // 3. Fallback inteligente si no hay IA o falla la conexión
        String respuestaLocal = generarRespuestaLocal(mensajeUsuario, todas, contextoPendientes);
        return ChatMessageResponseDTO.builder()
                .reply(respuestaLocal)
                .suggestions(generarSugerencias())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String recopilarContextoPendientes(List<Cotizacion> todas) {
        StringBuilder sb = new StringBuilder();
        try {
            List<Cotizacion> sinLiquidacion = cotizacionRepository.findCotizacionesSinLiquidacion();
            List<Cotizacion> sinCobranza = cotizacionRepository.findCotizacionesSinDocumentoCobranza();

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime en7Dias = ahora.plusDays(7);

            // Filtrar cotizaciones próximas a vencer
            List<Cotizacion> proximasVencer = todas.stream()
                    .filter(c -> c.getFechaVencimiento() != null &&
                            c.getFechaVencimiento().isAfter(ahora.minusDays(2)) &&
                            c.getFechaVencimiento().isBefore(en7Dias))
                    .limit(5)
                    .collect(Collectors.toList());

            // Ordenar descendentemente por ID o fecha para obtener verdaderamente las más recientes
            List<Cotizacion> recientes = todas.stream()
                    .sorted((c1, c2) -> Long.compare(c2.getId(), c1.getId()))
                    .limit(6)
                    .collect(Collectors.toList());

            sb.append("ESTADO GENERAL DEL SISTEMA:\n");
            sb.append("- Total registradas: ").append(todas.size()).append("\n");
            sb.append("- Sin liquidación: ").append(sinLiquidacion.size()).append("\n");
            sb.append("- Sin documento de cobranza: ").append(sinCobranza.size()).append("\n\n");

            if (!proximasVencer.isEmpty()) {
                sb.append("COTIZACIONES PRÓXIMAS A VENCER:\n");
                for (Cotizacion c : proximasVencer) {
                    sb.append("• [").append(c.getCodigoCotizacion() != null ? c.getCodigoCotizacion() : "ID: " + c.getId()).append("] ")
                            .append(c.getNombreCotizacion() != null ? c.getNombreCotizacion() : "Sin título")
                            .append(" - Destino: ").append(c.getOrigenDestino() != null ? c.getOrigenDestino() : "N/D")
                            .append(" - Vence: ").append(formatearFecha(c.getFechaVencimiento()))
                            .append(" - Estado: ").append(c.getEstadoCotizacion() != null ? c.getEstadoCotizacion().getDescripcion() : "Pendiente")
                            .append("\n");
                }
                sb.append("\n");
            }

            if (!recientes.isEmpty()) {
                sb.append("ÚLTIMAS COTIZACIONES CREADAS (De más nueva a más antigua):\n");
                for (Cotizacion c : recientes) {
                    sb.append("• [").append(c.getCodigoCotizacion() != null ? c.getCodigoCotizacion() : "ID: " + c.getId()).append("] ")
                            .append(c.getNombreCotizacion() != null ? c.getNombreCotizacion() : "Cotización")
                            .append(" (").append(c.getMoneda() != null ? c.getMoneda() : "USD").append(")")
                            .append(" - Estado: ").append(c.getEstadoCotizacion() != null ? c.getEstadoCotizacion().getDescripcion() : "Activa")
                            .append("\n");
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo cargar todo el contexto de cotizaciones: {}", e.getMessage());
            sb.append("No se pudo obtener el listado detallado de cotizaciones en este momento.");
        }
        return sb.toString();
    }

    private String llamarGeminiAPI(String mensajeUsuario, String contexto) {
        String endpoint = geminiApiUrl + "?key=" + geminiApiKey;

        String systemInstruction = "Eres 'EveryBot', el asistente de EveryWhere Travel.\n" +
                "Tienes acceso a estos datos en tiempo real:\n\n" + contexto + "\n\n" +
                "REGLAS OBLIGATORIAS:\n" +
                "1. Responde DE FORMA DIRECTA a lo que pregunta el usuario.\n" +
                "2. Si pide 'la última cotización' o 'la más reciente', menciona ÚNICAMENTE la primera cotización de la lista de recientes (su código, nombre, moneda y estado).\n" +
                "3. PROHIBIDO imprimir el resumen general ('ESTADO GENERAL DEL SISTEMA') a menos que el usuario use palabras como 'resumen', 'estado global', 'reporte' o 'hola'.\n" +
                "4. Sé conciso, directo y usa formato Markdown amigable.";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", mensajeUsuario))
                        )
                ),
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "maxOutputTokens", 800
                )
        );

        RestClient restClient = RestClient.builder().build();

        String rawResponse = restClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extraerTextoDeGemini(rawResponse);
    }

    private String extraerTextoDeGemini(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }
        } catch (Exception e) {
            log.error("Error al parsear respuesta de Gemini: {}", e.getMessage());
        }
        return null;
    }

    private String generarRespuestaLocal(String mensajeUsuario, List<Cotizacion> todas, String contexto) {
        String msg = mensajeUsuario.toLowerCase();

        // Responder específicamente a la solicitud de última cotización en modo offline
        if (msg.contains("ultima") || msg.contains("última") || msg.contains("reciente")) {
            Optional<Cotizacion> ultimaOpt = todas.stream()
                    .max(Comparator.comparing(Cotizacion::getId));

            if (ultimaOpt.isPresent()) {
                Cotizacion c = ultimaOpt.get();
                String codigo = c.getCodigoCotizacion() != null ? c.getCodigoCotizacion() : "ID: " + c.getId();
                String nombre = c.getNombreCotizacion() != null ? c.getNombreCotizacion() : "Cotización sin título";
                String estado = c.getEstadoCotizacion() != null ? c.getEstadoCotizacion().getDescripcion() : "Sin estado";
                String moneda = c.getMoneda() != null ? c.getMoneda() : "USD";

                return String.format("📋 **Última Cotización Registrada:**\n\n" +
                        "• **Código:** %s\n" +
                        "• **Nombre:** %s\n" +
                        "• **Moneda:** %s\n" +
                        "• **Estado:** %s", codigo, nombre, moneda, estado);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("¡Hola! Soy **EveryBot**, tu asistente de **EveryWhere Travel**.\n\n");

        if (msg.contains("venc") || msg.contains("urgent")) {
            sb.append("⏳ **Prioridades y Vencimientos:**\n");
            sb.append("Revisa las cotizaciones con fecha límite esta semana para evitar vencimientos de tarifa.\n\n");
        } else if (msg.contains("liquid") || msg.contains("tarea")) {
            sb.append("📑 **Tareas Pendientes:**\n");
            sb.append("Tienes cotizaciones pendientes de liquidación y documentos de cobranza por generar.\n\n");
        } else {
            sb.append("Aquí tienes tu resumen operativo actual:\n\n");
            sb.append(contexto);
        }

        return sb.toString();
    }

    private List<String> generarSugerencias() {
        return List.of(
                "Dame la última cotización",
                "¿Qué cotizaciones vencen pronto?",
                "¿Cuáles faltan liquidar?",
                "Redactar un mensaje de seguimiento"
        );
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "Sin fecha";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
