package com.everywhere.backend.api;

import com.everywhere.backend.model.entity.ConfiguracionApi;
import com.everywhere.backend.service.ConfiguracionApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ConfiguracionApiService configuracionApiService;

    @GetMapping("/tipo-de-cambio")
    public ResponseEntity<Map<String, String>> getTipoDeCambio() {
        log.info("Solicitando tipo de cambio oficial de SUNAT");
        Map<String, String> rates = configuracionApiService.getTipoDeCambioSunat();
        if (!rates.containsKey("buy") || !rates.containsKey("sell")) {
            return new ResponseEntity<>(rates, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }

    @GetMapping("/config")
    public ResponseEntity<ConfiguracionApi> getConfig() {
        return ResponseEntity.ok(configuracionApiService.getConfiguracion());
    }

    @PostMapping("/config")
    public ResponseEntity<ConfiguracionApi> saveConfig(@RequestBody ConfiguracionApi config) {
        return ResponseEntity.ok(configuracionApiService.saveConfiguracion(config));
    }
}