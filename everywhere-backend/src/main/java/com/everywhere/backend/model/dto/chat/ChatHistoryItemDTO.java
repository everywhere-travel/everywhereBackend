package com.everywhere.backend.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryItemDTO {
    private String sender; // "user" | "bot"
    private String text;
}
