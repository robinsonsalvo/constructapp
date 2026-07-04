package com.constructapp.ms_proyecto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Servicio de validacion de tokens JWT.
 * Este microservicio NO emite tokens (eso lo hace ms-auth); solo valida
 * la firma y vigencia de los tokens recibidos, usando el mismo secreto
 * compartido configurado via la propiedad jwt.secret (debe coincidir
 * con el de ms-auth para que la firma sea valida).
 */
@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return !esTokenExpirado(token);
        } catch (Exception e) {
            log.error("Token invalido: {}", e.getMessage());
            return false;
        }
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
