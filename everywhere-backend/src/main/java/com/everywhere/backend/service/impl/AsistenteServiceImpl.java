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

import java.time.LocalDate;
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

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String geminiApiUrl;

    @Override
    public ChatMessageResponseDTO procesarMensaje(ChatMessageRequestDTO request) {
        String mensajeUsuario = request.getMessage() != null ? request.getMessage().trim() : "";

        // 1. Recopilar datos reales de cotizaciones y tareas pendientes
        String contextoPendientes = recopilarContextoPendientes();

        // 2. Si hay API Key de Gemini configurada, intentar procesar con IA
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
                log.error("Error al comunicarse con Gemini API, usando respuesta asistida local: {}", e.getMessage());
            }
        }

        // 3. Fallback inteligente / respuesta asistida si no hay IA o falla la conexión
        String respuestaLocal = generarRespuestaLocal(mensajeUsuario, contextoPendientes);
        return ChatMessageResponseDTO.builder()
                .reply(respuestaLocal)
                .suggestions(generarSugerencias())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String recopilarContextoPendientes() {
        StringBuilder sb = new StringBuilder();
        try {
            List<Cotizacion> todas = cotizacionRepository.findAll();
            List<Cotizacion> sinLiquidacion = cotizacionRepository.findCotizacionesSinLiquidacion();
            List<Cotizacion> sinCobranza = cotizacionRepository.findCotizacionesSinDocumentoCobranza();

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime en7Dias = ahora.plusDays(7);

            // Filtrar cotizaciones pendientes o próximas a vencer
            List<Cotizacion> proximasVencer = todas.stream()
                    .filter(c -> c.getFechaVencimiento() != null &&
                            c.getFechaVencimiento().isAfter(ahora.minusDays(2)) &&
                            c.getFechaVencimiento().isBefore(en7Dias))
                    .limit(5)
                    .collect(Collectors.toList());

            // Últimas cotizaciones
            List<Cotizacion> recientes = todas.stream()
                    .limit(6)
                    .collect(Collectors.toList());

            sb.append("ESTADO ACTUAL DEL SISTEMA EVERYWHERE:\n");
            sb.append("- Total de cotizaciones registradas: ").append(todas.size()).append("\n");
            sb.append("- Cotizaciones pendientes de liquidación: ").append(sinLiquidacion.size()).append("\n");
            sb.append("- Cotizaciones pendientes de documento de cobranza: ").append(sinCobranza.size()).append("\n\n");

            if (!proximasVencer.isEmpty()) {
                sb.append("COTIZACIONES PRÓXIMAS A VENCER O RECIENTES:\n");
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
                sb.append("COTIZACIONES RECIENTES EN SEGUIMIENTO:\n");
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

        String systemInstruction = "Eres 'EveryBot', el copiloto inteligente de EveryWhere Travel (agencia de viajes en Perú).\n" +
                "Tu misión es ayudar a los asesores y administradores a gestionar sus cotizaciones, clientes, vencimientos y tareas administrativas pendientes.\n" +
                "Tienes acceso en tiempo real a los siguientes datos de la base de datos:\n\n" +
                contexto + "\n\n" +
                "Reglas de respuesta:\n" +
                "1. Sé conciso, profesional, amable y directo al grano.\n" +
                "2. Usa formato Markdown (negritas, viñetas, emojis relevantes como 📋, ⏳, ✈️).\n" +
                "3. Si el usuario pregunta por tareas o cotizaciones pendientes, prioriza aquellas que vencen pronto o requieren liquidación/cobranza.\n" +
                "4. Si te piden redactar un mensaje para un cliente o sugerir un itinerario, hazlo con un tono comercial impecable.";

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
                        "temperature", 0.6,
                        "maxOutputTokens", 1000
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

    private String generarRespuestaLocal(String mensajeUsuario, String contexto) {
        StringBuilder sb = new StringBuilder();
        sb.append("¡Hola! Soy **EveryBot**, tu asistente de **EveryWhere Travel**.\n\n");

        if (mensajeUsuario.toLowerCase().contains("venc") || mensajeUsuario.toLowerCase().contains("urgent")) {
            sb.append("⏳ **Prioridades y Vencimientos:**\n");
            sb.append("Te sugiero revisar las cotizaciones que tienen fecha límite esta semana para evitar que expiren las tarifas aéreas o de hotel.\n\n");
        } else if (mensajeUsuario.toLowerCase().contains("liquid") || mensajeUsuario.toLowerCase().contains("tarea")) {
            sb.append("📑 **Tareas Administrativas Pendientes:**\n");
            sb.append("Recuerda que tienes cotizaciones aprobadas pendientes de liquidación y generación de documentos de cobranza.\n\n");
        } else {
            sb.append("Aquí tienes tu resumen operativo actual:\n\n");
        }

        sb.append(contexto);
        sb.append("\n\n💡 *Puedes preguntarme por cotizaciones específicas, tareas pendientes o redactar mensajes para clientes.*");
        return sb.toString();
    }

    private List<String> generarSugerencias() {
        return List.of(
                "¿Qué cotizaciones tengo pendientes?",
                "¿Cuáles vencen pronto?",
                "¿Qué cotizaciones faltan liquidar?",
                "Ayúdame a redactar un seguimiento"
        );
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "Sin fecha";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
