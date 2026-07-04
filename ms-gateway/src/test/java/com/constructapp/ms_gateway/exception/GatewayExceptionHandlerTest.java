package com.constructapp.ms_gateway.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios del manejador global de excepciones del API Gateway.
 * Verifica que cada tipo de error se traduzca al código HTTP correcto
 * en la respuesta JSON entregada al cliente.
 */
@DisplayName("Tests del GatewayExceptionHandler")
class GatewayExceptionHandlerTest {

    private final GatewayExceptionHandler handler = new GatewayExceptionHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ServerWebExchange crearExchange(String path) {
        MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
        return MockServerWebExchange.from(request);
    }

    private Map<String, Object> leerCuerpo(ServerWebExchange exchange) throws IOException {
        MockServerWebExchange mockExchange = (MockServerWebExchange) exchange;
        String json = mockExchange.getResponse().getBodyAsString().block();
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    @Test
    @DisplayName("ResponseStatusException con 404 - devuelve 404 con el mensaje original")
    void handle_responseStatusException404_devuelveNotFound() throws IOException {
        ServerWebExchange exchange = crearExchange("/api/materiales/999");
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Material no encontrado");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
        Map<String, Object> body = leerCuerpo(exchange);
        assertEquals(404, body.get("status"));
        assertEquals("Material no encontrado", body.get("message"));
    }

    @Test
    @DisplayName("Conexion rechazada - devuelve 503 SERVICE_UNAVAILABLE")
    void handle_conexionRechazada_devuelveServiceUnavailable() throws IOException {
        ServerWebExchange exchange = crearExchange("/api/proyectos");
        Exception ex = new RuntimeException("Connection refused: ms-proyecto no responde");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        Map<String, Object> body = leerCuerpo(exchange);
        assertEquals(503, body.get("status"));
    }

    @Test
    @DisplayName("Error desconocido - devuelve 500 INTERNAL_SERVER_ERROR por defecto")
    void handle_errorDesconocido_devuelveInternalServerError() throws IOException {
        ServerWebExchange exchange = crearExchange("/api/cotizaciones");
        Exception ex = new IllegalStateException("Fallo inesperado");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
        Map<String, Object> body = leerCuerpo(exchange);
        assertEquals(500, body.get("status"));
        assertEquals("Error interno en el API Gateway", body.get("message"));
    }

    @Test
    @DisplayName("La respuesta siempre incluye la ruta solicitada")
    void handle_incluyeRutaSolicitada() throws IOException {
        ServerWebExchange exchange = crearExchange("/api/clientes/5");
        Exception ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos invalidos");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        Map<String, Object> body = leerCuerpo(exchange);
        assertEquals("/api/clientes/5", body.get("path"));
    }
}
