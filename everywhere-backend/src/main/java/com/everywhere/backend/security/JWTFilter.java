package com.everywhere.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;


@Component
@AllArgsConstructor
public class JWTFilter extends GenericFilterBean {

    private  final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String bearerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        // TODO: Verificar si el token no es nulo/vacío y si empieza con el prefijo "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Eliminar el prefijo "Bearer " para obtener solo el token
            String token = bearerToken.substring(7);

            // Validar que el token tenga el formato correcto de JWT (exactamente 2 puntos)
            if (isValidJwtFormat(token)) {
                try {
                    // TODO: Validar el token antes de procesarlo
                    if (tokenProvider.validateToken(token)) {
                        // TODO: Utilizar el TokenProvider para obtener la autenticación a partir del token JWT
                        Authentication authentication = tokenProvider.getAuthentication(token);

                        // TODO: Establecer la autenticación en el contexto de seguridad de Spring para la solicitud actual
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    // Log del error pero continuar con la cadena de filtros sin autenticación
                    logger.warn("Error al procesar el token JWT: " + e.getMessage());
                }
            } else {
                logger.warn("Token JWT con formato inválido. Puntos encontrados: " + countDots(token));
            }
        }

        // TODO: Continuar con la cadena de filtros, permitiendo que la solicitud siga su curso
        chain.doFilter(request, response);
    }

    /**
     * Valida que el token tenga el formato correcto de JWT (exactamente 2 puntos)
     */
    private boolean isValidJwtFormat(String token) {
        return token != null && countDots(token) == 2;
    }

    /**
     * Cuenta el número de puntos en el token
     */
    private int countDots(String token) {
        if (token == null) return 0;
        return (int) token.chars().filter(ch -> ch == '.').count();
    }

}
