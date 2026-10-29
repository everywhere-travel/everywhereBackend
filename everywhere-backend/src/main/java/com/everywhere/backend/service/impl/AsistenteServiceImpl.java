package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.analytics.AnalyticsDashboardDTO;
import com.everywhere.backend.model.dto.chat.ChatHistoryItemDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageRequestDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageResponseDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.service.AnalyticsService;
import com.everywhere.backend.service.AsistenteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent}")
    private String geminiApiUrl;

    @Override
    public ChatMessageResponseDTO procesarMensaje(ChatMessageRequestDTO request) {
        String mensajeUsuario = request.getMessage() != null ? request.getMessage().trim() : "";

        // 1. Recopilar datos completos y enriquecidos en tiempo real
        List<Cotizacion> todas = cotizacionRepository.findAll();
        Map<Integer, BigDecimal> totalesMap = calcularTotalesPorCotizacion();
        Map<Integer, CotizacionMapper.ClienteInfo> clientesInfo = resolverClientes(todas);

        List<Cotizacion> sinLiquidacion = cotizacionRepository.findCotizacionesSinLiquidacion();
        List<Cotizacion> sinCobranza = cotizacionRepository.findCotizacionesSinDocumentoCobranza();

        // Cotización más cara y más barata
        Cotizacion masCara = todas.stream()
                .filter(c -> totalesMap.containsKey(c.getId()) && totalesMap.get(c.getId()).compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparing(c -> totalesMap.get(c.getId())))
                .orElse(null);

        Cotizacion masBarata = todas.stream()
                .filter(c -> totalesMap.containsKey(c.getId()) && totalesMap.get(c.getId()).compareTo(BigDecimal.ZERO) > 0)
                .min(Comparator.comparing(c -> totalesMap.get(c.getId())))
                .orElse(null);

        // Métricas acumuladas
        BigDecimal sumaTotalCotizada = totalesMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadConPrecio = totalesMap.values().stream()
                .filter(m -> m.compareTo(BigDecimal.ZERO) > 0)
                .count();

        BigDecimal ticketPromedio = cantidadConPrecio > 0
                ? sumaTotalCotizada.divide(BigDecimal.valueOf(cantidadConPrecio), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime en7Dias = ahora.plusDays(7);
        List<Cotizacion> proximasVencer = todas.stream()
                .filter(c -> c.getFechaVencimiento() != null &&
                        c.getFechaVencimiento().isAfter(ahora.minusDays(1)) &&
                        c.getFechaVencimiento().isBefore(en7Dias))
                .sorted(Comparator.comparing(Cotizacion::getFechaVencimiento))
                .limit(5)
                .toList();

        List<Cotizacion> recientes = todas.stream()
                .sorted((c1, c2) -> Long.compare(c2.getId(), c1.getId()))
                .limit(6)
                .toList();

        List<Map.Entry<String, Long>> topDestinos = calcularTopDestinos(todas);

        // Contexto textual para Gemini
        String contexto = construirContextoCompleto(
                todas, totalesMap, clientesInfo, masCara, masBarata,
                sumaTotalCotizada, ticketPromedio, sinLiquidacion, sinCobranza,
                proximasVencer, recientes, topDestinos
        );

        // 2. Procesar con Gemini API si la API Key está configurada
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                String respuestaIA = llamarGeminiAPI(request, contexto);
                if (respuestaIA != null && !respuestaIA.isBlank()) {
                    return ChatMessageResponseDTO.builder()
                            .reply(respuestaIA)
                            .suggestions(generarSugerencias(mensajeUsuario))
                            .timestamp(LocalDateTime.now())
                            .build();
                }
            } catch (Exception e) {
                log.error("Error al comunicarse con Gemini API, usando motor local enriquecido: {}", e.getMessage());
            }
        }

        // 3. Motor local enriquecido (fallback dinámico inteligente con datos reales)
        String respuestaLocal = generarRespuestaLocal(
                mensajeUsuario, todas, totalesMap, clientesInfo,
                masCara, masBarata, sumaTotalCotizada, ticketPromedio,
                proximasVencer, recientes, sinLiquidacion, sinCobranza, topDestinos
        );

        return ChatMessageResponseDTO.builder()
                .reply(respuestaLocal)
                .suggestions(generarSugerencias(mensajeUsuario))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Map<Integer, BigDecimal> calcularTotalesPorCotizacion() {
        Map<Integer, BigDecimal> totalesMap = new HashMap<>();
        try {
            List<Object[]> filas = detalleCotizacionRepository.findTotalesPorCotizacion();
            for (Object[] fila : filas) {
                if (fila != null && fila.length >= 3 && fila[0] != null) {
                    Integer cotId = ((Number) fila[0]).intValue();
                    BigDecimal sumSel = fila[1] != null ? new BigDecimal(fila[1].toString()) : BigDecimal.ZERO;
                    BigDecimal sumTot = fila[2] != null ? new BigDecimal(fila[2].toString()) : BigDecimal.ZERO;
                    BigDecimal total = sumSel.compareTo(BigDecimal.ZERO) > 0 ? sumSel : sumTot;
                    totalesMap.put(cotId, total);
                }
            }
        } catch (Exception e) {
            log.warn("No se pudieron consultar totales de cotizaciones: {}", e.getMessage());
        }
        return totalesMap;
    }

    private Map<Integer, CotizacionMapper.ClienteInfo> resolverClientes(List<Cotizacion> todas) {
        try {
            return cotizacionMapper.resolveClienteInfo(todas);
        } catch (Exception e) {
            log.warn("No se pudieron resolver nombres de clientes: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private List<Map.Entry<String, Long>> calcularTopDestinos(List<Cotizacion> todas) {
        return todas.stream()
                .map(Cotizacion::getOrigenDestino)
                .filter(d -> d != null && !d.isBlank())
                .collect(Collectors.groupingBy(d -> d.trim().toUpperCase(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(4)
                .toList();
    }

    private String construirContextoCompleto(
            List<Cotizacion> todas,
            Map<Integer, BigDecimal> totalesMap,
            Map<Integer, CotizacionMapper.ClienteInfo> clientesInfo,
            Cotizacion masCara,
            Cotizacion masBarata,
            BigDecimal sumaTotalCotizada,
            BigDecimal ticketPromedio,
            List<Cotizacion> sinLiquidacion,
            List<Cotizacion> sinCobranza,
            List<Cotizacion> proximasVencer,
            List<Cotizacion> recientes,
            List<Map.Entry<String, Long>> topDestinos
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("--- MÉTRICAS GENERALES EN TIEMPO REAL ---\n");
        sb.append("• Total de cotizaciones en el sistema: ").append(todas.size()).append("\n");
        sb.append(String.format("• Volumen total cotizado acumulado: USD %,.2f\n", sumaTotalCotizada));
        sb.append(String.format("• Ticket promedio por cotización: USD %,.2f\n", ticketPromedio));
        sb.append("• Cotizaciones pendientes de liquidar: ").append(sinLiquidacion.size()).append("\n");
        sb.append("• Cotizaciones sin documento de cobranza: ").append(sinCobranza.size()).append("\n\n");

        if (masCara != null) {
            BigDecimal monto = totalesMap.getOrDefault(masCara.getId(), BigDecimal.ZERO);
            String cli = obtenerNombreCliente(masCara, clientesInfo);
            sb.append("--- COTIZACIÓN DE MAYOR MONTO (MÁS CARA) ---\n");
            sb.append("• Código: ").append(obtenerCodigo(masCara)).append("\n");
            sb.append("• Nombre: ").append(obtenerNombre(masCara)).append("\n");
            sb.append("• Cliente: ").append(cli).append("\n");
            sb.append(String.format("• Monto: %s %,.2f\n", masCara.getMoneda() != null ? masCara.getMoneda() : "USD", monto));
            sb.append("• Destino: ").append(obtenerDestino(masCara)).append("\n");
            sb.append("• Estado: ").append(obtenerEstado(masCara)).append("\n");
            sb.append("• Vence: ").append(formatearFecha(masCara.getFechaVencimiento())).append("\n\n");
        }

        if (masBarata != null) {
            BigDecimal monto = totalesMap.getOrDefault(masBarata.getId(), BigDecimal.ZERO);
            String cli = obtenerNombreCliente(masBarata, clientesInfo);
            sb.append("--- COTIZACIÓN MÁS ECONÓMICA ---\n");
            sb.append("• Código: ").append(obtenerCodigo(masBarata)).append(" - ").append(obtenerNombre(masBarata));
            sb.append(" | Cliente: ").append(cli);
            sb.append(String.format(" | Monto: %s %,.2f\n\n", masBarata.getMoneda() != null ? masBarata.getMoneda() : "USD", monto));
        }

        if (!proximasVencer.isEmpty()) {
            sb.append("--- COTIZACIONES PRÓXIMAS A VENCER (Próximos 7 días) ---\n");
            for (Cotizacion c : proximasVencer) {
                BigDecimal monto = totalesMap.getOrDefault(c.getId(), BigDecimal.ZERO);
                sb.append("• [").append(obtenerCodigo(c)).append("] ")
                        .append(obtenerNombre(c))
                        .append(" | Cliente: ").append(obtenerNombreCliente(c, clientesInfo))
                        .append(String.format(" | Monto: %s %,.2f", c.getMoneda() != null ? c.getMoneda() : "USD", monto))
                        .append(" | Vence: ").append(formatearFecha(c.getFechaVencimiento()))
                        .append(" | Estado: ").append(obtenerEstado(c))
                        .append("\n");
            }
            sb.append("\n");
        }

        if (!recientes.isEmpty()) {
            sb.append("--- ÚLTIMAS COTIZACIONES REGISTRADAS ---\n");
            for (Cotizacion c : recientes) {
                BigDecimal monto = totalesMap.getOrDefault(c.getId(), BigDecimal.ZERO);
                sb.append("• [").append(obtenerCodigo(c)).append("] ")
                        .append(obtenerNombre(c))
                        .append(" | Cliente: ").append(obtenerNombreCliente(c, clientesInfo))
                        .append(String.format(" | Monto: %s %,.2f", c.getMoneda() != null ? c.getMoneda() : "USD", monto))
                        .append(" | Estado: ").append(obtenerEstado(c))
                        .append("\n");
            }
            sb.append("\n");
        }

        if (!topDestinos.isEmpty()) {
            sb.append("--- DESTINOS MÁS SOLICITADOS ---\n");
            for (Map.Entry<String, Long> entry : topDestinos) {
                sb.append("• ").append(entry.getKey()).append(" (").append(entry.getValue()).append(" cotizaciones)\n");
            }
            sb.append("\n");
        }

        // Incorporar KPIs comerciales de Analytics si están disponibles
        try {
            LocalDate hoy = LocalDate.now();
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            AnalyticsDashboardDTO analytics = analyticsService.getDashboardData(inicioMes, hoy);
            if (analytics != null && analytics.getKpis() != null) {
                sb.append("--- VENTAS CERRADAS DEL MES ACTUAL (Analytics) ---\n");
                sb.append(String.format("• Ventas cerradas: %d\n", analytics.getKpis().getTotalVentasCerradas()));
                sb.append(String.format("• Ingreso total facturado: USD %,.2f\n", analytics.getKpis().getIngresoTotal()));
                sb.append(String.format("• Ticket promedio de venta: USD %,.2f\n", analytics.getKpis().getExpedienteDeVenta()));
            }
        } catch (Exception e) {
            log.debug("No se pudieron añadir métricas de analytics al contexto: {}", e.getMessage());
        }

        return sb.toString();
    }

    private String llamarGeminiAPI(ChatMessageRequestDTO request, String contexto) {
        String endpoint = geminiApiUrl + "?key=" + geminiApiKey;

        String systemInstruction =
                "Eres 'EveryBot', el asistente inteligente de EveryWhere Travel. Tu personalidad es profesional, entusiasta, ágil y proactiva, como un asesor comercial de viajes de primer nivel.\n\n" +
                "DISPONES DE DATOS REALES DE LA AGENCIA EN TIEMPO REAL:\n\n" + contexto + "\n\n" +
                "REGLAS OBLIGATORIAS:\n" +
                "1. Responde de forma precisa, conversacional y con excelente formato Markdown.\n" +
                "2. Si te preguntan por la cotización 'más cara', 'de mayor monto' o 'más barata', indica con exactitud el Código, Nombre, Cliente, Monto con su Moneda, Destino y Estado. Añade una sugerencia proactiva (por ejemplo: ofrecer redactar un mensaje de seguimiento para el cliente).\n" +
                "3. Si el usuario te pide redactar un mensaje, recordatorio o correo de seguimiento (para WhatsApp o email), escribe una propuesta comercial persuasiva, elegante y lista para enviar al cliente.\n" +
                "4. Usa emojis relevantes (✈️, 💰, 📋, ⏳, 🌟) para dar dinamismo sin sobrecargar.\n" +
                "5. Ten en cuenta el historial previo de la conversación para responder con continuidad a preguntas encadenadas (ej. '¿y cuándo vence?', '¿quién es el cliente?').\n" +
                "6. Sé conciso pero completo; evita rodeos innecesarios.";

        List<Map<String, Object>> contents = new ArrayList<>();

        // Si hay historial, incluir los últimos mensajes garantizando alternancia
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            int start = Math.max(0, request.getHistory().size() - 6);
            List<ChatHistoryItemDTO> recentHistory = request.getHistory().subList(start, request.getHistory().size());

            String lastRole = null;
            for (ChatHistoryItemDTO item : recentHistory) {
                if (item.getText() != null && !item.getText().isBlank()) {
                    String role = "user".equalsIgnoreCase(item.getSender()) ? "user" : "model";
                    if (!role.equals(lastRole)) {
                        contents.add(Map.of("role", role, "parts", List.of(Map.of("text", item.getText()))));
                        lastRole = role;
                    }
                }
            }
        }

        // Asegurar que la conversación inicie con 'user' si hay historial
        if (!contents.isEmpty() && "model".equals(contents.get(0).get("role"))) {
            contents.remove(0);
        }

        // Agregar el mensaje actual del usuario
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", request.getMessage() != null ? request.getMessage() : "Hola"))
        ));

        Map<String, Object> requestBody = Map.of(
                "contents", contents,
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.35,
                        "maxOutputTokens", 900
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

    private String generarRespuestaLocal(
            String mensajeUsuario,
            List<Cotizacion> todas,
            Map<Integer, BigDecimal> totalesMap,
            Map<Integer, CotizacionMapper.ClienteInfo> clientesInfo,
            Cotizacion masCara,
            Cotizacion masBarata,
            BigDecimal sumaTotalCotizada,
            BigDecimal ticketPromedio,
            List<Cotizacion> proximasVencer,
            List<Cotizacion> recientes,
            List<Cotizacion> sinLiquidacion,
            List<Cotizacion> sinCobranza,
            List<Map.Entry<String, Long>> topDestinos
    ) {
        String msg = mensajeUsuario.toLowerCase();

        // 1. Cotización más cara / mayor valor
        if (msg.contains("cara") || msg.contains("mayor monto") || msg.contains("mas alto") || msg.contains("más alto")
                || msg.contains("mayor valor") || msg.contains("top cotiz") || msg.contains("mas cara") || msg.contains("más cara")
                || msg.contains("mayor precio") || msg.contains("costosa")) {

            if (masCara != null) {
                BigDecimal monto = totalesMap.getOrDefault(masCara.getId(), BigDecimal.ZERO);
                String cliente = obtenerNombreCliente(masCara, clientesInfo);
                String moneda = masCara.getMoneda() != null ? masCara.getMoneda() : "USD";

                return String.format(
                        "💎 **Cotización de Mayor Valor Registrada:**\n\n" +
                        "• **Código:** `%s`\n" +
                        "• **Título:** %s\n" +
                        "• **Cliente:** **%s**\n" +
                        "• **Monto:** **%s %,.2f**\n" +
                        "• **Destino:** %s\n" +
                        "• **Pasajeros:** %d adulto(s)%s\n" +
                        "• **Estado:** %s\n" +
                        "• **Vencimiento:** %s\n\n" +
                        "💡 *Tip comercial:* Al ser una cotización de alto ticket, un contacto cercano es clave para cerrarla. ¿Te gustaría que prepare un mensaje de seguimiento personalizado para enviarle al cliente?",
                        obtenerCodigo(masCara),
                        obtenerNombre(masCara),
                        cliente,
                        moneda, monto,
                        obtenerDestino(masCara),
                        masCara.getCantAdultos() != null ? masCara.getCantAdultos() : 1,
                        masCara.getCantNinos() != null && masCara.getCantNinos() > 0 ? " | " + masCara.getCantNinos() + " niño(s)" : "",
                        obtenerEstado(masCara),
                        formatearFecha(masCara.getFechaVencimiento())
                );
            } else {
                return "ℹ️ Aún no hay cotizaciones con montos registrados para determinar la de mayor valor.";
            }
        }

        // 2. Cotización más económica / barata
        if (msg.contains("barat") || msg.contains("econom") || msg.contains("económ") || msg.contains("menor monto") || msg.contains("menor precio")) {
            if (masBarata != null) {
                BigDecimal monto = totalesMap.getOrDefault(masBarata.getId(), BigDecimal.ZERO);
                String cliente = obtenerNombreCliente(masBarata, clientesInfo);
                String moneda = masBarata.getMoneda() != null ? masBarata.getMoneda() : "USD";

                return String.format(
                        "🏷️ **Cotización Más Económica:**\n\n" +
                        "• **Código:** `%s`\n" +
                        "• **Título:** %s\n" +
                        "• **Cliente:** %s\n" +
                        "• **Monto:** **%s %,.2f**\n" +
                        "• **Destino:** %s\n" +
                        "• **Estado:** %s",
                        obtenerCodigo(masBarata),
                        obtenerNombre(masBarata),
                        cliente,
                        moneda, monto,
                        obtenerDestino(masBarata),
                        obtenerEstado(masBarata)
                );
            }
        }

        // 3. Totales, ticket promedio o KPIs financieros
        if (msg.contains("total") || msg.contains("promedio") || msg.contains("ticket") || msg.contains("cuanto hay") || msg.contains("cuánto hay") || msg.contains("estadistica") || msg.contains("estadística") || msg.contains("kpi")) {
            return String.format(
                    "📊 **Resumen Financiero de Cotizaciones:**\n\n" +
                    "• **Total Registradas:** %d cotizaciones\n" +
                    "• **Volumen Total Cotizado:** **USD %,.2f**\n" +
                    "• **Ticket Promedio:** **USD %,.2f** por cotización\n" +
                    "• **Pendientes de Liquidación:** %d\n" +
                    "• **Pendientes de Cobro:** %d\n\n" +
                    "¿Deseas ver las cotizaciones que vencen esta semana o revisar la de mayor importe?",
                    todas.size(), sumaTotalCotizada, ticketPromedio, sinLiquidacion.size(), sinCobranza.size()
            );
        }

        // 4. Redacción de mensajes de seguimiento / correo
        if (msg.contains("redact") || msg.contains("seguimiento") || msg.contains("mensaje") || msg.contains("correo") || msg.contains("email") || msg.contains("whatsapp") || msg.contains("plantilla")) {
            return "✍️ **Plantilla de Seguimiento Comercial (Lista para WhatsApp o Correo):**\n\n" +
                   "────────────────────────────────\n" +
                   "*Hola [Nombre del Cliente], ¡buenos días!* 👋\n\n" +
                   "*Te saluda [Tu Nombre] de EveryWhere Travel. Te escribo para dar seguimiento a la propuesta de viaje que preparamos con destino a [Destino].*\n\n" +
                   "*Actualmente tenemos las tarifas y disponibilidad confirmadas con los operadores, pero tienen una fecha límite de reserva para asegurar los mejores precios. ¿Tuviste oportunidad de revisarla o te gustaría que adaptemos algún hotel o itinerario?*\n\n" +
                   "*Quedo muy atento/a para resolver cualquier duda y ayudarte a concretar tu viaje.* ✈️🌟\n" +
                   "────────────────────────────────\n\n" +
                   "¿Quieres que adapte este mensaje para alguna cotización en específico?";
        }

        // 5. Destinos más cotizados
        if (msg.contains("destino") || msg.contains("donde") || msg.contains("dónde") || msg.contains("viaja") || msg.contains("popular")) {
            if (!topDestinos.isEmpty()) {
                StringBuilder sb = new StringBuilder("🌍 **Destinos Más Solicitados en EveryWhere:**\n\n");
                for (int i = 0; i < topDestinos.size(); i++) {
                    Map.Entry<String, Long> d = topDestinos.get(i);
                    sb.append(String.format("%d. **%s** — %d cotizaciones registradas\n", i + 1, d.getKey(), d.getValue()));
                }
                sb.append("\n¿Deseas consultar cotizaciones abiertas para alguno de estos destinos?");
                return sb.toString();
            }
        }

        // 6. Última cotización registrada
        if (msg.contains("ultima") || msg.contains("última") || msg.contains("reciente") || msg.contains("nueva")) {
            if (!recientes.isEmpty()) {
                Cotizacion c = recientes.get(0);
                BigDecimal monto = totalesMap.getOrDefault(c.getId(), BigDecimal.ZERO);
                String cliente = obtenerNombreCliente(c, clientesInfo);
                String moneda = c.getMoneda() != null ? c.getMoneda() : "USD";

                return String.format(
                        "📋 **Última Cotización Registrada:**\n\n" +
                        "• **Código:** `%s`\n" +
                        "• **Título:** %s\n" +
                        "• **Cliente:** **%s**\n" +
                        "• **Monto:** **%s %,.2f**\n" +
                        "• **Destino:** %s\n" +
                        "• **Estado:** %s\n" +
                        "• **Fecha Emisión:** %s",
                        obtenerCodigo(c),
                        obtenerNombre(c),
                        cliente,
                        moneda, monto,
                        obtenerDestino(c),
                        obtenerEstado(c),
                        formatearFecha(c.getFechaEmision())
                );
            }
        }

        // 7. Próximas a vencer
        if (msg.contains("venc") || msg.contains("urgent") || msg.contains("caduc") || msg.contains("plazo")) {
            if (!proximasVencer.isEmpty()) {
                StringBuilder sb = new StringBuilder("⏳ **Cotizaciones Próximas a Vencer (Atención Requerida):**\n\n");
                for (Cotizacion c : proximasVencer) {
                    BigDecimal monto = totalesMap.getOrDefault(c.getId(), BigDecimal.ZERO);
                    sb.append(String.format("• **[%s]** %s\n  — Cliente: %s | Monto: %s %,.2f\n  — Vence: **%s** (%s)\n\n",
                            obtenerCodigo(c),
                            obtenerNombre(c),
                            obtenerNombreCliente(c, clientesInfo),
                            c.getMoneda() != null ? c.getMoneda() : "USD", monto,
                            formatearFecha(c.getFechaVencimiento()),
                            obtenerEstado(c)));
                }
                sb.append("💡 *Recomendación:* Contacta a estos clientes hoy para evitar la pérdida de tarifas promocionales.");
                return sb.toString();
            } else {
                return "✅ **¡Buenas noticias!** No hay cotizaciones próximas a vencer en los siguientes 7 días.";
            }
        }

        // 8. Tareas pendientes (Liquidación / Cobranza)
        if (msg.contains("liquid") || msg.contains("cobran") || msg.contains("tarea") || msg.contains("pendiente")) {
            return String.format(
                    "📑 **Control de Tareas y Pendientes Administrativos:**\n\n" +
                    "• **Cotizaciones sin Liquidación:** **%d** pendientes\n" +
                    "• **Cotizaciones sin Documento de Cobranza:** **%d** pendientes\n\n" +
                    "Mantener estos registros al día garantiza reportes financieros precisos y facturación oportuna. ¿Quieres revisar detalles de alguna de ellas?",
                    sinLiquidacion.size(), sinCobranza.size()
            );
        }

        // 9. Saludo o fallback proactivo y conversacional
        StringBuilder sb = new StringBuilder();
        sb.append("¡Hola! Soy **EveryBot**, tu copiloto en **EveryWhere Travel** ✈️\n\n");
        sb.append("Actualmente tenemos **").append(todas.size()).append("** cotizaciones registradas por un volumen de **USD ")
                .append(String.format("%,.2f", sumaTotalCotizada)).append("**.\n\n");

        if (masCara != null) {
            BigDecimal monto = totalesMap.getOrDefault(masCara.getId(), BigDecimal.ZERO);
            sb.append(String.format("💎 La cotización más destacada es **%s** (%s) por **USD %,.2f**.\n",
                    obtenerCodigo(masCara), obtenerNombre(masCara), monto));
        }

        if (!proximasVencer.isEmpty()) {
            sb.append("⏳ Tienes **").append(proximasVencer.size()).append("** cotizaciones con fecha límite esta semana.\n\n");
        }

        sb.append("¿En qué te puedo apoyar hoy? Puedes preguntarme por la **cotización más cara**, pedirme **redactar un seguimiento**, consultar **vencimientos** o ver **estadísticas**.");
        return sb.toString();
    }

    private List<String> generarSugerencias(String mensajeUsuario) {
        String msg = mensajeUsuario != null ? mensajeUsuario.toLowerCase() : "";

        if (msg.contains("cara") || msg.contains("monto") || msg.contains("precio")) {
            return List.of(
                    "✍️ Redactar seguimiento para este cliente",
                    "⏳ ¿Qué cotizaciones vencen pronto?",
                    "📊 Ver ticket promedio y totales",
                    "🌍 ¿Cuáles son los destinos más pedidos?"
            );
        }

        if (msg.contains("venc") || msg.contains("urgent")) {
            return List.of(
                    "💎 ¿Cuál es la cotización más cara?",
                    "✍️ Redactar mensaje de recordatorio",
                    "📑 ¿Cuáles faltan liquidar?",
                    "📊 Resumen financiero general"
            );
        }

        if (msg.contains("redact") || msg.contains("mensaje") || msg.contains("correo")) {
            return List.of(
                    "💎 ¿Cuál es la cotización más cara?",
                    "⏳ ¿Qué cotizaciones vencen pronto?",
                    "🌍 Destinos más cotizados",
                    "📋 ¿Cuál fue la última cotización?"
            );
        }

        return List.of(
                "💎 ¿Cuál es la cotización más cara?",
                "⏳ ¿Qué cotizaciones vencen pronto?",
                "📊 Resumen y ticket promedio",
                "✍️ Redactar seguimiento comercial"
        );
    }

    private String obtenerCodigo(Cotizacion c) {
        return c.getCodigoCotizacion() != null && !c.getCodigoCotizacion().isBlank()
                ? c.getCodigoCotizacion()
                : "ID: " + c.getId();
    }

    private String obtenerNombre(Cotizacion c) {
        return c.getNombreCotizacion() != null && !c.getNombreCotizacion().isBlank()
                ? c.getNombreCotizacion()
                : "Cotización de Viaje";
    }

    private String obtenerDestino(Cotizacion c) {
        return c.getOrigenDestino() != null && !c.getOrigenDestino().isBlank()
                ? c.getOrigenDestino()
                : "No especificado";
    }

    private String obtenerEstado(Cotizacion c) {
        return c.getEstadoCotizacion() != null && c.getEstadoCotizacion().getDescripcion() != null
                ? c.getEstadoCotizacion().getDescripcion()
                : "Activa";
    }

    private String obtenerNombreCliente(Cotizacion c, Map<Integer, CotizacionMapper.ClienteInfo> clientesInfo) {
        if (c.getPersonas() != null && clientesInfo.containsKey(c.getPersonas().getId())) {
            CotizacionMapper.ClienteInfo info = clientesInfo.get(c.getPersonas().getId());
            if (info != null && info.nombre() != null && !info.nombre().isBlank()) {
                return info.nombre();
            }
        }
        return "Cliente sin asignar";
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "Sin fecha límite";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
