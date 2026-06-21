package com.constructapp.ms_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-2) // Prioridad alta para adelantarse al manejador por defecto de Spring Boot
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String mensaje = "Error interno en el API Gateway";

        // Controlar si el error es un código de estado HTTP conocido (444, 404, etc.)
        if (ex instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            mensaje = responseStatusException.getReason();
        } 
        // Controlar si el microservicio destino está caído totalmente
        else if (ex.getMessage() != null && (ex.getMessage().contains("Connection refused") || ex.getMessage().contains("Connection timed out"))) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            mensaje = "El microservicio solicitado no se encuentra disponible momentáneamente.";
        }

        response.setStatusCode(status);

        // Estructura de respuesta de error limpia
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", mensaje);
        errorDetails.put("path", exchange.getRequest().getPath().value());

        log.error("Gateway Exception interceptada: {} en la ruta: {}", ex.getMessage(), exchange.getRequest().getPath().value());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorDetails);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el mapa de errores", e);
            return Mono.error(e);
        }
    }
}