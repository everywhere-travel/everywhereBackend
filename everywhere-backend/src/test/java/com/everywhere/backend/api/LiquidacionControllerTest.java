package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.service.LiquidacionService;
import com.everywhere.backend.utils.JsonUtil;
import com.everywhere.backend.utils.LiquidacionTestData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LiquidacionControllerTest extends BaseControllerTest {

    @Mock
    private LiquidacionService liquidacionService;

    @InjectMocks
    private LiquidacionController liquidacionController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        setupMockMvc(liquidacionController);
    }

    @Test
    void should_Return200AndList_When_GetAllLiquidaciones() throws Exception {
        List<LiquidacionResponseDTO> list = Collections.singletonList(LiquidacionTestData.getValidLiquidacionResponseDTO());
        when(liquidacionService.findAll()).thenReturn(list);

        mockMvc.perform(get("/liquidaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numero").value("LIQ-2023-001"))
                .andExpect(jsonPath("$[0].destino").value("Cancun"));
    }

    @Test
    void should_Return200AndLiquidacion_When_GetLiquidacionById_Exists() throws Exception {
        LiquidacionResponseDTO responseDTO = LiquidacionTestData.getValidLiquidacionResponseDTO();
        when(liquidacionService.findById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/liquidaciones/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value("LIQ-2023-001"));
    }

    @Test
    void should_Return404_When_GetLiquidacionById_NotExists() throws Exception {
        when(liquidacionService.findById(99)).thenThrow(new ResourceNotFoundException("Liquidación no encontrada"));

        mockMvc.perform(get("/liquidaciones/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Liquidación no encontrada"));
    }

    @Test
    void should_Return201AndLiquidacion_When_CreateConCotizacion() throws Exception {
        LiquidacionRequestDTO requestDTO = LiquidacionTestData.getValidLiquidacionRequestDTO();
        LiquidacionResponseDTO responseDTO = LiquidacionTestData.getValidLiquidacionResponseDTO();

        when(liquidacionService.create(any(LiquidacionRequestDTO.class), eq(1))).thenReturn(responseDTO);

        mockMvc.perform(post("/liquidaciones/cotizacion/{cotizacionId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value("LIQ-2023-001"));
    }

    @Test
    void should_Return200AndLiquidacion_When_Update() throws Exception {
        LiquidacionRequestDTO requestDTO = LiquidacionTestData.getValidLiquidacionRequestDTO();
        LiquidacionResponseDTO responseDTO = LiquidacionTestData.getValidLiquidacionResponseDTO();

        when(liquidacionService.update(eq(1), any(LiquidacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/liquidaciones/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return204_When_Delete() throws Exception {
        doNothing().when(liquidacionService).deleteById(1);

        mockMvc.perform(delete("/liquidaciones/{id}", 1))
                .andExpect(status().isNoContent());

        verify(liquidacionService, times(1)).deleteById(1);
    }

    @Test
    void should_Return200AndLiquidacionConDetalles_When_GetLiquidacionConDetalles() throws Exception {
        com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO responseDTO = new com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO();
        when(liquidacionService.findByIdWithDetalles(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/liquidaciones/{id}/con-detalles", 1))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndExcelFile_When_GenerateExcel() throws Exception {
        java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream("mock excel".getBytes());
        when(liquidacionService.generateExcel(1)).thenReturn(stream);

        mockMvc.perform(get("/liquidaciones/{id}/generar-excel", 1))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"Liquidacion_1.xlsx\""))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    void should_Return200AndList_When_FindByCarpeta() throws Exception {
        when(liquidacionService.findByCarpeta(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/liquidaciones/carpeta/{carpetaId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndList_When_FindSinCarpeta() throws Exception {
        when(liquidacionService.findSinCarpeta()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/liquidaciones/sin-carpeta"))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndLiquidacion_When_UpdateCarpeta() throws Exception {
        LiquidacionResponseDTO responseDto = LiquidacionTestData.getValidLiquidacionResponseDTO();
        when(liquidacionService.updateCarpeta(1, 2)).thenReturn(responseDto);

        mockMvc.perform(patch("/liquidaciones/{id}/carpeta", 1)
                .param("carpetaId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
