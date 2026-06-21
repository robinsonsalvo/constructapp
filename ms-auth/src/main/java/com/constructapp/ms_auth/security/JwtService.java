package com.constructapp.ms_auth.security;

import com.constructapp.ms_auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Genera el token JWT con los datos del usuario
    public String generarToken(Usuario usuario) {
        log.info("Generando token para usuario: {}", usuario.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        claims.put("id", usuario.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el username del token
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    // Extrae el rol del token
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    // Verifica si el token es válido
    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return !esTokenExpirado(token);
        } catch (Exception e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    // Verifica si el token está expirado
    private boolean esTokenExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    // Extrae todos los claims del token
    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Genera la clave de firma desde el secret
    private Key getKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}