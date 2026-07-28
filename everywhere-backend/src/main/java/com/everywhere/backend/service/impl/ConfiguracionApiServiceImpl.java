package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.entity.ConfiguracionApi;
import com.everywhere.backend.repository.ConfiguracionApiRepository;
import com.everywhere.backend.service.ConfiguracionApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionApiServiceImpl implements ConfiguracionApiService {

    private final ConfiguracionApiRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionApi getConfiguracion() {
        Optional<ConfiguracionApi> configOpt = repository.findFirstByActivoTrueOrderByIdDesc();
        if (configOpt.isPresent()) {
            return configOpt.get();
        }
        // Configuración por defecto de APIPeru SUNAT si aún no existe en base de datos
        ConfiguracionApi defaultConfig = new ConfiguracionApi();
        defaultConfig.setUrl("https://apiperu.dev/api/tipo_de_cambio");
        defaultConfig.setToken("");
        defaultConfig.setActivo(true);
        return defaultConfig;
    }

    @Override
    @Transactional
    public ConfiguracionApi saveConfiguracion(ConfiguracionApi config) {
        Optional<ConfiguracionApi> existingOpt = repository.findFirstByActivoTrueOrderByIdDesc();
        ConfiguracionApi target;
        if (existingOpt.isPresent()) {
            target = existingOpt.get();
            target.setUrl(config.getUrl());
            target.setToken(config.getToken());
            target.setActivo(true);
        } else {
            target = config;
            target.setActivo(true);
        }
        log.info("Guardando configuración de APIPeru SUNAT. URL: {}", target.getUrl());
        return repository.save(target);
    }

    @Override
    public Map<String, String> getTipoDeCambioSunat() {
        Map<String, String> rates = new HashMap<>();
        ConfiguracionApi config = getConfiguracion();

        if (config.getUrl() == null || config.getUrl().trim().isEmpty() ||
            config.getToken() == null || config.getToken().trim().isEmpty()) {
            log.warn("API Peru SUNAT no está configurado (falta token o url).");
            rates.put("error", "Token de APIPeru no configurado");
            return rates;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + config.getToken().trim());

        LocalDate dateToQuery = LocalDate.now(ZoneId.of("America/Lima"));
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // Intentar la fecha de hoy, y si es fin de semana/festivo sin cotización, retroceder hasta 3 días
        for (int i = 0; i <= 3; i++) {
            String fechaStr = dateToQuery.minusDays(i).format(formatter);
            try {
                String requestBody = "{\"fecha\": \"" + fechaStr + "\"}";
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                log.info("Consultando tipo de cambio SUNAT (APIPeru) para la fecha: {} en {}", fechaStr, config.getUrl());
                ResponseEntity<String> response = restTemplate.exchange(
                        config.getUrl(), HttpMethod.POST, entity, String.class
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    boolean success = root.path("success").asBoolean(false);
                    JsonNode dataNode = root.path("data");

                    if (success && dataNode != null && !dataNode.isMissingNode() && !dataNode.isNull()) {
                        String compra = dataNode.has("compra") ? dataNode.path("compra").asText() : dataNode.path("purchase").asText();
                        String venta = dataNode.has("venta") ? dataNode.path("venta").asText() : dataNode.path("sale").asText();

                        if (!compra.isEmpty() && !venta.isEmpty() && !compra.equals("null") && !compra.equals("0")) {
                            rates.put("buy", compra);
                            rates.put("sell", venta);
                            rates.put("date", fechaStr);
                            rates.put("source", "SUNAT_API_PERU");
                            log.info("Cotización SUNAT exitosa - Fecha: {}, Compra: {}, Venta: {}", fechaStr, compra, venta);
                            return rates;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Fallo al consultar APIPeru SUNAT para la fecha {}: {}", fechaStr, e.getMessage());
            }
        }

        log.warn("No se pudo obtener la cotización de APIPeru en los últimos 4 días (verificar validez del token o conexión).");
        rates.put("error", "No se pudo consultar SUNAT (verifique su Token y conexión)");
        return rates;
    }
}
