package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalViajeroDTO;
import com.everywhere.backend.service.PersonaNaturalService;
import com.everywhere.backend.utils.JsonUtil;
import com.everywhere.backend.utils.PersonaTestData;
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
class PersonaNaturalControllerTest extends BaseControllerTest {

    @Mock
    private PersonaNaturalService personaNaturalService;

    @InjectMocks
    private PersonaNaturalController personaNaturalController;

    @BeforeEach
    void setUp() {
        setupMockMvc(personaNaturalController);
    }

    @Test
    void should_Return200AndList_When_GetAllPersonasNaturales() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.findAll()).thenReturn(list);

        mockMvc.perform(get("/personas-naturales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].documento").value("77778888"));
    }

    @Test
    void should_Return200AndList_When_GetDropdown() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.getDropdown("Juan")).thenReturn(list);

        mockMvc.perform(get("/personas-naturales/dropdown").param("search", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByDocumento() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.findByDocumento("77778888")).thenReturn(list);

        mockMvc.perform(get("/personas-naturales/documento").param("documento", "77778888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByNombre() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.findByNombres("Juan")).thenReturn(list);

        mockMvc.perform(get("/personas-naturales/nombres").param("nombres", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByApellidoPaterno() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.findByApellidosPaternos("Perez")).thenReturn(list);

        mockMvc.perform(get("/personas-naturales/apellidos-paterno").param("apellidos", "Perez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndList_When_GetByApellidoMaterno() throws Exception {
        List<PersonaNaturalResponseDTO> list = Collections.singletonList(PersonaTestData.getValidNaturalResponse());
        when(personaNaturalService.findByApellidosMaternos("Gomez")).thenReturn(list);

        mockMvc.perform(get("/personas-naturales/apellidos-materno").param("apellidos", "Gomez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void should_Return200AndPersona_When_GetPersonaNaturalById() throws Exception {
        PersonaNaturalResponseDTO responseDTO = PersonaTestData.getValidNaturalResponse();
        when(personaNaturalService.findById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/personas-naturales/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.documento").value("77778888"));
    }

    @Test
    void should_Return404_When_GetPersonaNaturalById_NotExists() throws Exception {
        when(personaNaturalService.findById(99)).thenThrow(new ResourceNotFoundException("Persona no encontrada"));

        mockMvc.perform(get("/personas-naturales/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void should_Return201AndPersona_When_CreatePersonaNatural() throws Exception {
        PersonaNaturalRequestDTO request = PersonaTestData.getValidNaturalRequest();
        PersonaNaturalResponseDTO response = PersonaTestData.getValidNaturalResponse();

        when(personaNaturalService.save(any(PersonaNaturalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/personas-naturales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    void should_Return200AndPersona_When_PatchPersonaNatural() throws Exception {
        PersonaNaturalRequestDTO request = PersonaTestData.getValidNaturalRequest();
        PersonaNaturalResponseDTO response = PersonaTestData.getValidNaturalResponse();

        when(personaNaturalService.patch(eq(1), any(PersonaNaturalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/personas-naturales/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return200_When_AsociarViajero() throws Exception {
        PersonaNaturalViajeroDTO viajeroDTO = new PersonaNaturalViajeroDTO();
        viajeroDTO.setViajeroId(5);
        
        PersonaNaturalResponseDTO response = PersonaTestData.getValidNaturalResponse();
        when(personaNaturalService.asociarViajero(1, 5)).thenReturn(response);

        mockMvc.perform(patch("/personas-naturales/{id}/asociar-viajero", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(viajeroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return200_When_DesasociarViajero() throws Exception {
        PersonaNaturalResponseDTO response = PersonaTestData.getValidNaturalResponse();
        when(personaNaturalService.desasociarViajero(1)).thenReturn(response);

        mockMvc.perform(patch("/personas-naturales/{id}/desasociar-viajero", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_Return204_When_DeletePersonaNatural() throws Exception {
        doNothing().when(personaNaturalService).deleteById(1);

        mockMvc.perform(delete("/personas-naturales/{id}", 1))
                .andExpect(status().isNoContent());

        verify(personaNaturalService, times(1)).deleteById(1);
    }
}
