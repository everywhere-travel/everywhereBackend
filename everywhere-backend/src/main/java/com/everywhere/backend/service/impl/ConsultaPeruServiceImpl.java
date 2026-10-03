package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.model.dto.consulta.DniConsultaResponseDTO;
import com.everywhere.backend.model.dto.consulta.RucConsultaResponseDTO;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.repository.ConfiguracionApiRepository;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.service.ConsultaPeruService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaPeruServiceImpl implements ConsultaPeruService {

    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final ConfiguracionApiRepository configuracionApiRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${apiperu.url:https://api.apiperu.dev}")
    private String apiperuUrl;

    @Value("${apiperu.token:}")
    private String apiperuToken;

    @Override
    @Transactional(readOnly = true)
    public DniConsultaResponseDTO consultarDni(String dni) {
        if (dni == null || !dni.trim().matches("^\\d{8}$")) {
            throw new BadRequestException("El número de DNI debe contener exactamente 8 dígitos numéricos.");
        }
        String cleanDni = dni.trim();

        // 1. Búsqueda local: ¿Existe ya en persona_natural o detalle_documento?
        Optional<PersonaNatural> naturalOpt = personaNaturalRepository.findByDocumentoIgnoreCase(cleanDni);
        if (naturalOpt.isPresent()) {
            PersonaNatural pn = naturalOpt.get();
            String nombreCompleto = construirNombreCompleto(pn.getNombres(), pn.getApellidosPaterno(), pn.getApellidosMaterno());
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .nombres(pn.getNombres())
                    .apellidoPaterno(pn.getApellidosPaterno())
                    .apellidoMaterno(pn.getApellidosMaterno())
                    .nombreCompleto(nombreCompleto)
                    .origen("LOCAL")
                    .encontrado(true)
                    .personaNaturalId(pn.getId())
                    .personaId(pn.getPersonas() != null ? pn.getPersonas().getId() : null)
                    .mensaje("Persona encontrada en el sistema local")
                    .build();
        }

        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroContainingIgnoreCase(cleanDni);
        for (DetalleDocumento det : detalles) {
            if (det.getNumero() != null && det.getNumero().trim().equalsIgnoreCase(cleanDni) && det.getPersonaNatural() != null) {
                PersonaNatural pn = det.getPersonaNatural();
                String nombreCompleto = construirNombreCompleto(pn.getNombres(), pn.getApellidosPaterno(), pn.getApellidosMaterno());
                return DniConsultaResponseDTO.builder()
                        .dni(cleanDni)
                        .nombres(pn.getNombres())
                        .apellidoPaterno(pn.getApellidosPaterno())
                        .apellidoMaterno(pn.getApellidosMaterno())
                        .nombreCompleto(nombreCompleto)
                        .origen("LOCAL")
                        .encontrado(true)
                        .personaNaturalId(pn.getId())
                        .personaId(pn.getPersonas() != null ? pn.getPersonas().getId() : null)
                        .mensaje("Persona encontrada mediante documento registrado en el sistema")
                        .build();
            }
        }

        // 2. Consulta externa a ApiPeru.dev
        String token = resolverToken();
        if (token.isBlank()) {
            log.warn("Consulta DNI {}: No existe localmente y no se ha configurado APIPERU_TOKEN en el entorno.", cleanDni);
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("LOCAL")
                    .mensaje("El DNI no está registrado localmente y el token de ApiPeru no ha sido configurado en el servidor.")
                    .build();
        }

        String endpoint = buildEndpoint("/dni");
        HttpHeaders headers = createHeaders(token);
        String requestBody = "{\"dni\":\"" + cleanDni + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("[ApiPeru] Iniciando consulta DNI {} -> Endpoint: {} | RequestBody: {}", cleanDni, endpoint, requestBody);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
            log.info("[ApiPeru] Respuesta recibida para DNI {}: Status={} | Body={}", cleanDni, response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                if (success && root.has("data")) {
                    JsonNode data = root.path("data");
                    String nombres = data.path("nombres").asText("");
                    String apellidoPat = data.path("apellido_paterno").asText("");
                    String apellidoMat = data.path("apellido_materno").asText("");
                    String nombreCompleto = data.path("nombre_completo").asText(construirNombreCompleto(nombres, apellidoPat, apellidoMat));
                    Integer codVerif = data.has("codigo_verificacion") ? data.path("codigo_verificacion").asInt() : null;

                    return DniConsultaResponseDTO.builder()
                            .dni(cleanDni)
                            .nombres(nombres)
                            .apellidoPaterno(apellidoPat)
                            .apellidoMaterno(apellidoMat)
                            .nombreCompleto(nombreCompleto)
                            .codigoVerificacion(codVerif)
                            .origen("APIPERU")
                            .encontrado(true)
                            .mensaje("Datos obtenidos exitosamente de RENIEC / Padrón")
                            .build();
                } else {
                    String msg = root.path("message").asText("El DNI no fue encontrado en los registros oficiales.");
                    log.warn("[ApiPeru] Consulta no exitosa (success=false) para DNI {}: msg='{}' | RawBody={}", cleanDni, msg, response.getBody());
                    return DniConsultaResponseDTO.builder()
                            .dni(cleanDni)
                            .encontrado(false)
                            .origen("APIPERU")
                            .mensaje(msg + " [Respuesta: " + response.getBody() + "]")
                            .build();
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[ApiPeru] DNI {} no encontrado (404 Not Found): Body={}", cleanDni, e.getResponseBodyAsString());
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("El DNI ingresado no existe en los registros oficiales (404: " + e.getResponseBodyAsString() + ")")
                    .build();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("[ApiPeru] Error 401 Unauthorized al consultar DNI {}: Token inválido o correo no verificado. Respuesta: {}", cleanDni, e.getResponseBodyAsString());
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Error de autenticación con ApiPeru (401 Unauthorized). Respuesta: " + e.getResponseBodyAsString())
                    .build();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("[ApiPeru] Límite de consultas excedido (429 Too Many Requests) para DNI {}: Body={}", cleanDni, e.getResponseBodyAsString());
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Has alcanzado el límite de consultas de tu plan en ApiPeru (429: " + e.getResponseBodyAsString() + ")")
                    .build();
        } catch (HttpStatusCodeException e) {
            log.error("[ApiPeru] Error HTTP {} al consultar DNI {}: Body={}", e.getStatusCode(), cleanDni, e.getResponseBodyAsString());
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Error HTTP " + e.getStatusCode() + " de ApiPeru: " + e.getResponseBodyAsString())
                    .build();
        } catch (Exception e) {
            log.error("[ApiPeru] Error inesperado al consultar DNI {}: {}", cleanDni, e.getMessage(), e);
            return DniConsultaResponseDTO.builder()
                    .dni(cleanDni)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Ocurrió un error al consultar el servicio de RENIEC: " + e.getMessage())
                    .build();
        }

        return DniConsultaResponseDTO.builder()
                .dni(cleanDni)
                .encontrado(false)
                .origen("APIPERU")
                .mensaje("No se obtuvieron resultados para el DNI.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RucConsultaResponseDTO consultarRuc(String ruc) {
        if (ruc == null || !ruc.trim().matches("^\\d{11}$")) {
            throw new BadRequestException("El número de RUC debe contener exactamente 11 dígitos numéricos.");
        }
        String cleanRuc = ruc.trim();

        // 1. Búsqueda local: ¿Existe ya en persona_juridica?
        Optional<PersonaJuridica> juridicaOpt = personaJuridicaRepository.findByRucIgnoreCase(cleanRuc);
        if (juridicaOpt.isPresent()) {
            PersonaJuridica pj = juridicaOpt.get();
            String direccion = pj.getPersonas() != null ? pj.getPersonas().getDireccion() : null;
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .razonSocial(pj.getRazonSocial())
                    .estado("ACTIVO")
                    .condicion("HABIDO")
                    .direccion(direccion)
                    .origen("LOCAL")
                    .encontrado(true)
                    .personaJuridicaId(pj.getId())
                    .personaId(pj.getPersonas() != null ? pj.getPersonas().getId() : null)
                    .mensaje("Empresa encontrada en el sistema local")
                    .build();
        }

        // 2. Consulta externa a ApiPeru.dev
        String token = resolverToken();
        if (token.isBlank()) {
            log.warn("Consulta RUC {}: No existe localmente y no se ha configurado APIPERU_TOKEN en el entorno.", cleanRuc);
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("LOCAL")
                    .mensaje("El RUC no está registrado localmente y el token de ApiPeru no ha sido configurado en el servidor.")
                    .build();
        }

        String endpoint = buildEndpoint("/ruc");
        HttpHeaders headers = createHeaders(token);
        String requestBody = "{\"ruc\":\"" + cleanRuc + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("[ApiPeru] Iniciando consulta RUC {} -> Endpoint: {} | RequestBody: {}", cleanRuc, endpoint, requestBody);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
            log.info("[ApiPeru] Respuesta recibida para RUC {}: Status={} | Body={}", cleanRuc, response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                if (success && root.has("data")) {
                    JsonNode data = root.path("data");
                    String razonSocial = data.path("nombre_o_razon_social").asText("");
                    String estado = data.path("estado").asText("");
                    String condicion = data.path("condicion").asText("");
                    String direccion = data.path("direccion").asText("");
                    String departamento = data.path("departamento").asText("");

                    return RucConsultaResponseDTO.builder()
                            .ruc(cleanRuc)
                            .razonSocial(razonSocial)
                            .estado(estado)
                            .condicion(condicion)
                            .direccion(direccion)
                            .departamento(departamento)
                            .origen("APIPERU")
                            .encontrado(true)
                            .mensaje("Datos obtenidos exitosamente de SUNAT")
                            .build();
                } else {
                    String msg = root.path("message").asText("El RUC no fue encontrado en los registros de SUNAT.");
                    log.warn("[ApiPeru] Consulta no exitosa (success=false) para RUC {}: msg='{}' | RawBody={}", cleanRuc, msg, response.getBody());
                    return RucConsultaResponseDTO.builder()
                            .ruc(cleanRuc)
                            .encontrado(false)
                            .origen("APIPERU")
                            .mensaje(msg + " [Respuesta: " + response.getBody() + "]")
                            .build();
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[ApiPeru] RUC {} no encontrado (404 Not Found): Body={}", cleanRuc, e.getResponseBodyAsString());
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("El RUC ingresado no existe en SUNAT (404: " + e.getResponseBodyAsString() + ")")
                    .build();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("[ApiPeru] Error 401 Unauthorized al consultar RUC {}: Token inválido o correo no verificado. Respuesta: {}", cleanRuc, e.getResponseBodyAsString());
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Error de autenticación con ApiPeru (401 Unauthorized). Respuesta: " + e.getResponseBodyAsString())
                    .build();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("[ApiPeru] Límite de consultas excedido (429 Too Many Requests) para RUC {}: Body={}", cleanRuc, e.getResponseBodyAsString());
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Has alcanzado el límite de consultas de tu plan en ApiPeru (429: " + e.getResponseBodyAsString() + ")")
                    .build();
        } catch (HttpStatusCodeException e) {
            log.error("[ApiPeru] Error HTTP {} al consultar RUC {}: Body={}", e.getStatusCode(), cleanRuc, e.getResponseBodyAsString());
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Error HTTP " + e.getStatusCode() + " de ApiPeru: " + e.getResponseBodyAsString())
                    .build();
        } catch (Exception e) {
            log.error("[ApiPeru] Error inesperado al consultar RUC {}: {}", cleanRuc, e.getMessage(), e);
            return RucConsultaResponseDTO.builder()
                    .ruc(cleanRuc)
                    .encontrado(false)
                    .origen("APIPERU")
                    .mensaje("Ocurrió un error al consultar el servicio de SUNAT: " + e.getMessage())
                    .build();
        }

        return RucConsultaResponseDTO.builder()
                .ruc(cleanRuc)
                .encontrado(false)
                .origen("APIPERU")
                .mensaje("No se obtuvieron resultados para el RUC.")
                .build();
    }

    private String resolverToken() {
        String token = "";
        String fuente = "NINGUNA";
        if (apiperuToken != null && !apiperuToken.trim().isEmpty()) {
            token = apiperuToken.trim();
            fuente = "ENV/PROPERTIES";
        } else {
            Optional<com.everywhere.backend.model.entity.ConfiguracionApi> configOpt = configuracionApiRepository.findFirstByActivoTrueOrderByIdDesc();
            if (configOpt.isPresent() && configOpt.get().getToken() != null && !configOpt.get().getToken().trim().isEmpty()) {
                token = configOpt.get().getToken().trim();
                fuente = "BD (configuracion_api)";
            }
        }

        if (token.isEmpty()) {
            log.warn("[ApiPeru] Token no encontrado ni en variables de entorno ni en BD.");
        } else {
            String masked = token.length() > 8 ? token.substring(0, 4) + "..." + token.substring(token.length() - 4) : "***";
            log.info("[ApiPeru] Token resuelto desde {}: {} (longitud: {})", fuente, masked, token.length());
        }
        return token;
    }

    private String buildEndpoint(String path) {
        String base = apiperuUrl != null && !apiperuUrl.trim().isEmpty() ? apiperuUrl.trim() : "https://api.apiperu.dev";
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (base.contains("apiperu.dev") && !base.contains("api.apiperu.dev") && !base.endsWith("/api")) {
            base += "/api";
        }
        return base + (path.startsWith("/") ? path : "/" + path);
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private String construirNombreCompleto(String nombres, String apePaterno, String apeMaterno) {
        StringBuilder sb = new StringBuilder();
        if (nombres != null && !nombres.isBlank()) sb.append(nombres.trim());
        if (apePaterno != null && !apePaterno.isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(apePaterno.trim());
        }
        if (apeMaterno != null && !apeMaterno.isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(apeMaterno.trim());
        }
        return sb.toString();
    }
}
