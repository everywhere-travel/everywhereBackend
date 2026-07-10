package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.model.dto.SaldoDocumentoCobranzaDTO;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.utils.CobranzaTestData;
import com.everywhere.backend.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentoCobranzaControllerTest extends BaseControllerTest {

    @Mock
    private DocumentoCobranzaService documentoCobranzaService;

    @InjectMocks
    private DocumentoCobranzaController documentoCobranzaController;

    @BeforeEach
    void setUp() {
        setupMockMvc(documentoCobranzaController);
    }

    @Test
    void should_Return200AndList_When_GetAllDocumentos() throws Exception {
        List<DocumentoCobranzaResponseDTO> list = Collections.singletonList(CobranzaTestData.getValidDocumentoCobranzaResponseDTO());
        when(documentoCobranzaService.findAll()).thenReturn(list);

        mockMvc.perform(get("/documentos-cobranza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serie").value("F001"));
    }

    @Test
    void should_Return200AndDocumento_When_GetDocumentoById_Exists() throws Exception {
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        when(documentoCobranzaService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/documentos-cobranza/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serie").value("F001"));
    }

    @Test
    void should_Return404_When_GetDocumentoById_NotExists() throws Exception {
        when(documentoCobranzaService.findById(99L)).thenThrow(new ResourceNotFoundException("Documento no encontrado"));

        mockMvc.perform(get("/documentos-cobranza/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Documento no encontrado"));
    }

    @Test
    void should_Return201AndDocumento_When_CreateDocumentoCobranza() throws Exception {
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        when(documentoCobranzaService.createDocumentoCobranza(eq(1), isNull(), isNull())).thenReturn(responseDTO);

        mockMvc.perform(post("/documentos-cobranza")
                        .param("cotizacionId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serie").value("F001"));
    }

    @Test
    void should_Return200AndDocumento_When_UpdateDocumento() throws Exception {
        DocumentoCobranzaUpdateDTO updateDTO = CobranzaTestData.getValidDocumentoCobranzaUpdateDTO();
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        
        when(documentoCobranzaService.patchDocumento(eq(1L), any(DocumentoCobranzaUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/documentos-cobranza/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return200AndSaldo_When_GetSaldo() throws Exception {
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        when(documentoCobranzaService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/documentos-cobranza/{id}/saldo", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentoCobranzaId").value(1))
                .andExpect(jsonPath("$.totalDeuda").value(1500.00))
                .andExpect(jsonPath("$.saldoPendiente").value(1000.00));
    }

    @Test
    void should_Return200AndSaldo_When_GetSaldo_NullValues() throws Exception {
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        responseDTO.setTotalDeuda(null);
        responseDTO.setTotalPagado(null);
        responseDTO.setSaldoPendiente(null);
        
        when(documentoCobranzaService.findById(2L)).thenReturn(responseDTO);

        mockMvc.perform(get("/documentos-cobranza/{id}/saldo", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeuda").value(0))
                .andExpect(jsonPath("$.totalPagado").value(0))
                .andExpect(jsonPath("$.saldoPendiente").value(0));
    }

    @Test
    void should_Return200AndList_When_GetDropdown() throws Exception {
        when(documentoCobranzaService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/documentos-cobranza/dropdown"))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndList_When_FindByCarpeta() throws Exception {
        when(documentoCobranzaService.findByCarpeta(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/documentos-cobranza/carpeta/{carpetaId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndList_When_FindSinCarpeta() throws Exception {
        when(documentoCobranzaService.findSinCarpeta()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/documentos-cobranza/sin-carpeta"))
                .andExpect(status().isOk());
    }

    @Test
    void should_Return200AndDocumento_When_UpdateCarpeta() throws Exception {
        DocumentoCobranzaResponseDTO responseDTO = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        when(documentoCobranzaService.updateCarpeta(1L, 2)).thenReturn(responseDTO);

        mockMvc.perform(patch("/documentos-cobranza/{id}/carpeta", 1L)
                .param("carpetaId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return200AndPage_When_GetDocumentosPage() throws Exception {
        DocumentoCobranzaResponseDTO dto = CobranzaTestData.getValidDocumentoCobranzaResponseDTO();
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<DocumentoCobranzaResponseDTO> page = new org.springframework.data.domain.PageImpl<>(
                java.util.Collections.singletonList(dto), pageable, 1);

        when(documentoCobranzaService.findPage(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/documentos-cobranza/page")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}
