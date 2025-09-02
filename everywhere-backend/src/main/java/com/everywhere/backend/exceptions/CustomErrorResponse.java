package com.everywhere.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
