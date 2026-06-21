package com.constructapp.ms_auth.service;

import com.constructapp.ms_auth.dto.LoginDTO;
import com.constructapp.ms_auth.dto.RegisterDTO;
import com.constructapp.ms_auth.dto.TokenResponseDTO;
import com.constructapp.ms_auth.model.Rol;
import com.constructapp.ms_auth.model.Usuario;
import com.constructapp.ms_auth.repository.UsuarioRepository;
import com.constructapp.ms_auth.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Autenticación")
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    private Usuario crearUsuario(Long id, String username) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername(username);
        u.setPassword("$2a$hashedpass");
        u.setRol(Rol.USER);
        return u;
    }

    @Test
    @DisplayName("registrar - registra usuario correctamente y retorna token")
    void registrar_datosValidos_retornaToken() {
        // Given
        when(usuarioRepository.existsByUsername("juan")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$hashed");
        when(usuarioRepository.save(any())).thenReturn(crearUsuario(1L, "juan"));
        when(jwtService.generarToken(any())).thenReturn("jwt-token-test");
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("juan");
        dto.setPassword("pass123");
        dto.setRol(Rol.USER);

        // When
        TokenResponseDTO resultado = authService.registrar(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("jwt-token-test", resultado.getToken());
        assertEquals("juan", resultado.getUsername());
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("registrar - lanza excepción si username ya existe")
    void registrar_usernameDuplicado_lanzaExcepcion() {
        // Given
        when(usuarioRepository.existsByUsername("juan")).thenReturn(true);
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("juan");
        dto.setPassword("pass123");
        dto.setRol(Rol.USER);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.registrar(dto));
        assertTrue(ex.getMessage().contains("juan"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("login - retorna token con credenciales válidas")
    void login_credencialesValidas_retornaToken() {
        // Given
        Usuario usuario = crearUsuario(1L, "juan");
        when(usuarioRepository.findByUsername("juan")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("pass123", "$2a$hashedpass")).thenReturn(true);
        when(jwtService.generarToken(any())).thenReturn("jwt-token-test");
        LoginDTO dto = new LoginDTO();
        dto.setUsername("juan");
        dto.setPassword("pass123");

        // When
        TokenResponseDTO resultado = authService.login(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("jwt-token-test", resultado.getToken());
        assertEquals("USER", resultado.getRol());
    }

    @Test
    @DisplayName("login - lanza excepción si usuario no existe")
    void login_usuarioNoExiste_lanzaExcepcion() {
        // Given
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());
        LoginDTO dto = new LoginDTO();
        dto.setUsername("noexiste");
        dto.setPassword("pass");

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.login(dto));
        assertTrue(ex.getMessage().contains("inválidas") || ex.getMessage().contains("invalidas"));
    }

    @Test
    @DisplayName("login - lanza excepción si contraseña es incorrecta")
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        // Given
        Usuario usuario = crearUsuario(1L, "juan");
        when(usuarioRepository.findByUsername("juan")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malapass", "$2a$hashedpass")).thenReturn(false);
        LoginDTO dto = new LoginDTO();
        dto.setUsername("juan");
        dto.setPassword("malapass");

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.login(dto));
        assertTrue(ex.getMessage().contains("inválidas") || ex.getMessage().contains("invalidas"));
    }

    @Test
    @DisplayName("validarToken - retorna true para token válido")
    void validarToken_valido_retornaTrue() {
        // Given
        when(jwtService.esTokenValido("valid-token")).thenReturn(true);

        // When
        boolean resultado = authService.validarToken("valid-token");

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("validarToken - retorna false para token inválido")
    void validarToken_invalido_retornaFalse() {
        // Given
        when(jwtService.esTokenValido("bad-token")).thenReturn(false);

        // When
        boolean resultado = authService.validarToken("bad-token");

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("extraerUsername - retorna el username del token")
    void extraerUsername_tokenValido_retornaUsername() {
        // Given
        when(jwtService.extraerUsername("token")).thenReturn("juan");

        // When
        String resultado = authService.extraerUsername("token");

        // Then
        assertEquals("juan", resultado);
    }

    @Test
    @DisplayName("extraerRol - retorna el rol del token")
    void extraerRol_tokenValido_retornaRol() {
        // Given
        when(jwtService.extraerRol("token")).thenReturn("ADMIN");

        // When
        String resultado = authService.extraerRol("token");

        // Then
        assertEquals("ADMIN", resultado);
    }
}
