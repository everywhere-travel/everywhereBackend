package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.EmailRequestDTO;

public interface EmailService {
    void sendEmail(EmailRequestDTO emailRequest, java.util.List<org.springframework.web.multipart.MultipartFile> files);
}
