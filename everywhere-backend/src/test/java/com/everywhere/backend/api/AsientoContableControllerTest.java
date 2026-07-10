package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;
import com.everywhere.backend.service.AsientoContableService;
import com.everywhere.backend.utils.AsientoContableTestData;
import com.everywhere.backend.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AsientoContableControllerTest extends BaseControllerTest {

    @Mock
    private AsientoContableService asientoService;

    @InjectMocks
    private AsientoContableController asientoContableController;

    @BeforeEach
    void setUp() {
        setupMockMvc(asientoContableController);
    }

    @Test
    void should_Return200AndList_When_Listar() throws Exception {
        List<AsientoContableResponseDTO> list = Collections.singletonList(AsientoContableTestData.getValidResponse());
        when(asientoService.listar()).thenReturn(list);

        mockMvc.perform(get("/asientos-contables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].glosa").value("Glosa de prueba"));
    }

    @Test
    void should_Return200AndAsiento_When_ObtenerPorId() throws Exception {
        AsientoContableResponseDTO responseDTO = AsientoContableTestData.getValidResponse();
        when(asientoService.obtenerPorId(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/asientos-contables/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.glosa").value("Glosa de prueba"));
    }

    @Test
    void should_Return404_When_ObtenerPorId_NotExists() throws Exception {
        when(asientoService.obtenerPorId(99)).thenThrow(new ResourceNotFoundException("Asiento no encontrado"));

        mockMvc.perform(get("/asientos-contables/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Asiento no encontrado"));
    }

    @Test
    void should_Return200AndList_When_ListarPorOrigen() throws Exception {
        List<AsientoContableResponseDTO> list = Collections.singletonList(AsientoContableTestData.getValidResponse());
        when(asientoService.listarPorOrigen("LIQUIDACION", 1)).thenReturn(list);

        mockMvc.perform(get("/asientos-contables/origen")
                .param("origen", "LIQUIDACION")
                .param("origenId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].origen").value("LIQUIDACION"));
    }

    @Test
    void should_Return201AndAsiento_When_Crear() throws Exception {
        AsientoContableRequestDTO request = AsientoContableTestData.getValidRequest();
        AsientoContableResponseDTO response = AsientoContableTestData.getValidResponse();

        when(asientoService.crear(any(AsientoContableRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/asientos-contables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.glosa").value("Glosa de prueba"));
    }

    @Test
    void should_Return204_When_Anular() throws Exception {
        doNothing().when(asientoService).anular(1);

        mockMvc.perform(put("/asientos-contables/{id}/anular", 1))
                .andExpect(status().isNoContent());
        
        verify(asientoService, times(1)).anular(1);
    }
}
