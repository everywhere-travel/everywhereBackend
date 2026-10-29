package com.everywhere.backend.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDTO {
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String message;

    private List<ChatHistoryItemDTO> history;
}
