package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.chat.ChatMessageRequestDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageResponseDTO;

public interface AsistenteService {
    ChatMessageResponseDTO procesarMensaje(ChatMessageRequestDTO request);
}
