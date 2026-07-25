package com.everywhere.backend.service;

import com.everywhere.backend.model.entity.ConfiguracionApi;
import java.util.Map;

public interface ConfiguracionApiService {
    ConfiguracionApi getConfiguracion();
    ConfiguracionApi saveConfiguracion(ConfiguracionApi config);
    Map<String, String> getTipoDeCambioSunat();
}
