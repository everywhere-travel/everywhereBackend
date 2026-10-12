package com.everywhere.backend.audit.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro HTTP que captura la IP de origen (con soporte para proxies / Docker / Contabo),
 * el endpoint y el User-Agent para inyectarlos en el AuditContextHolder.
 */
@Component
@Order(2)
public class AuditContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String ip = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String endpoint = request.getMethod() + " " + request.getRequestURI();

            AuditContextHolder.set(AuditContextHolder.AuditMetadata.builder()
                    .ipOrigen(ip)
                    .userAgent(userAgent != null ? (userAgent.length() > 290 ? userAgent.substring(0, 290) : userAgent) : "Desconocido")
                    .endpoint(endpoint != null ? (endpoint.length() > 250 ? endpoint.substring(0, 250) : endpoint) : "")
                    .build());

            filterChain.doFilter(request, response);
        } finally {
            AuditContextHolder.clear();
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            // Si viene una lista separada por comas, la primera es la IP real del cliente
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}
