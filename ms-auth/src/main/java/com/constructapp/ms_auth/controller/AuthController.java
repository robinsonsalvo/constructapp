package com.constructapp.ms_auth.controller;

import com.constructapp.ms_auth.dto.LoginDTO;
import com.constructapp.ms_auth.dto.RegisterDTO;
import com.constructapp.ms_auth.dto.TokenResponseDTO;
import com.constructapp.ms_auth.model.Usuario;
import com.constructapp.ms_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y validación de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Listar usuarios registrados", description = "Requiere token JWT válido en el header Authorization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarTodos() {
        log.info("GET /api/auth/listar");
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Roles disponibles: ADMIN, USER, PROVEEDOR")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado, retorna JWT"),
        @ApiResponse(responseCode = "400", description = "Username ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> registrar(@Valid @RequestBody RegisterDTO dto) {
        log.info("POST /api/auth/register");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @Operation(summary = "Iniciar sesión", description = "Retorna un JWT válido por 24 horas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        log.info("POST /api/auth/login");
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "Validar token JWT", description = "Enviar token en header: Authorization: Bearer {token}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validarToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("GET /api/auth/validate");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token no proporcionado"));
        }
        String token = authHeader.substring(7);
        boolean valido = authService.validarToken(token);
        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token inválido o expirado"));
        }
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", authService.extraerUsername(token),
                "rol", authService.extraerRol(token)));
    }
}
