package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;
import com.everywhere.backend.service.ReciboService;
import com.everywhere.backend.utils.JsonUtil;
import com.everywhere.backend.utils.ReciboTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReciboControllerTest extends BaseControllerTest {

    @Mock
    private ReciboService reciboService;

    @InjectMocks
    private ReciboController reciboController;

    @BeforeEach
    void setUp() {
        setupMockMvc(reciboController);
    }

    @Test
    void should_Return200AndList_When_GetAllRecibos() throws Exception {
        List<ReciboResponseDTO> list = Collections.singletonList(ReciboTestData.getValidReciboResponseDTO());
        when(reciboService.findAll()).thenReturn(list);

        mockMvc.perform(get("/recibos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serie").value("R001"));
    }

    @Test
    void should_Return200AndRecibo_When_GetReciboById_Exists() throws Exception {
        ReciboResponseDTO responseDTO = ReciboTestData.getValidReciboResponseDTO();
        when(reciboService.findById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/recibos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serie").value("R001"));
    }

    @Test
    void should_Return404_When_GetReciboById_NotExists() throws Exception {
        when(reciboService.findById(99)).thenThrow(new ResourceNotFoundException("Recibo no encontrado"));

        mockMvc.perform(get("/recibos/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Recibo no encontrado"));
    }

    @Test
    void should_Return201AndRecibo_When_CreateRecibo() throws Exception {
        ReciboResponseDTO responseDTO = ReciboTestData.getValidReciboResponseDTO();
        
        when(reciboService.createRecibo(eq(1), isNull(), isNull(), any(BigDecimal.class)))
            .thenReturn(responseDTO);

        mockMvc.perform(post("/recibos")
                        .param("documentoCobranzaId", "1")
                        .param("montoPago", "500.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serie").value("R001"));
    }

    @Test
    void should_Return200AndRecibo_When_UpdateRecibo() throws Exception {
        ReciboUpdateDTO updateDTO = ReciboTestData.getValidReciboUpdateDTO();
        ReciboResponseDTO responseDTO = ReciboTestData.getValidReciboResponseDTO();
        
        when(reciboService.patchRecibo(eq(1), any(ReciboUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/recibos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByDocumentoCobranzaId() throws Exception {
        List<ReciboResponseDTO> list = Collections.singletonList(ReciboTestData.getValidReciboResponseDTO());
        when(reciboService.findByDocumentoCobranzaId(1)).thenReturn(list);

        mockMvc.perform(get("/recibos/documento-cobranza/{documentoCobranzaId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentoCobranzaId").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByCotizacionId() throws Exception {
        List<ReciboResponseDTO> list = Collections.singletonList(ReciboTestData.getValidReciboResponseDTO());
        when(reciboService.findByCotizacionId(1)).thenReturn(list);

        mockMvc.perform(get("/recibos/cotizacion/{cotizacionId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndList_When_FindByCarpeta() throws Exception {
        when(reciboService.findByCarpeta(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recibos/carpeta/{carpetaId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndList_When_FindSinCarpeta() throws Exception {
        when(reciboService.findSinCarpeta()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recibos/sin-carpeta"))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndRecibo_When_UpdateCarpeta() throws Exception {
        ReciboResponseDTO responseDto = ReciboTestData.getValidReciboResponseDTO();
        when(reciboService.updateCarpeta(1, 2)).thenReturn(responseDto);

        mockMvc.perform(patch("/recibos/{id}/carpeta", 1)
                .param("carpetaId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
