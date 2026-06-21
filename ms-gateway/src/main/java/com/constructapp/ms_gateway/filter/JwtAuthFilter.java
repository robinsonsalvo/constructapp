package com.constructapp.ms_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final Key key;

    // Inyección fija por constructor para evitar el fallo original del ciclo de vida
    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Rutas públicas que no necesitan token
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // Obtener el token del header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Token no proporcionado para ruta: {}", path);
            return responderError(exchange, "Token no proporcionado");
        }

        String token = authHeader.substring(7);

        // Validar el token usando parserBuilder() (Sintaxis compatible JJWT 0.11.x)
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Token válido para usuario: {}", claims.getSubject());
            return chain.filter(exchange);

        } catch (Exception e) {
            log.error("Token inválido o expirado: {}", e.getMessage());
            return responderError(exchange, "Token inválido o expirado");
        }
    }

    private Mono<Void> responderError(ServerWebExchange exchange, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        byte[] bytes = ("{\"error\": \"" + mensaje + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(
                response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}