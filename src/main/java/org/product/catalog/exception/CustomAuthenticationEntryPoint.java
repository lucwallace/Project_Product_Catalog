package org.product.catalog.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());

        String message = "Erro de autenticação";

        if (ex.getMessage() != null) {

            String lower = ex.getMessage().toLowerCase();

            if (lower.contains("expired")) {
                message = "Token expirado";
            }
            else if (lower.contains("read timed out")) {
                message = "Timeout ao conectar no Keycloak";
            }
            else if (lower.contains("invalid")) {
                message = "Token inválido";
            }
        }

        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().write(
                mapper.writeValueAsString(errorResponse)
        );
    }
}