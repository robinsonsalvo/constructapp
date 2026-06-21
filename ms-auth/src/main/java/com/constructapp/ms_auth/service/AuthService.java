package com.constructapp.ms_auth.service;

import com.constructapp.ms_auth.dto.LoginDTO;
import com.constructapp.ms_auth.dto.RegisterDTO;
import com.constructapp.ms_auth.dto.TokenResponseDTO;
import com.constructapp.ms_auth.model.Usuario;
import com.constructapp.ms_auth.repository.UsuarioRepository;
import com.constructapp.ms_auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO registrar(RegisterDTO dto) {
        log.info("Registrando usuario: {}", dto.getUsername());

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con el username: "
                    + dto.getUsername());
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con id: {}", guardado.getId());

        String token = jwtService.generarToken(guardado);
        return new TokenResponseDTO(
                token,
                guardado.getUsername(),
                guardado.getRol().name(),
                86400000L
        );
    }

    public TokenResponseDTO login(LoginDTO dto) {
        log.info("Login para usuario: {}", dto.getUsername());

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException(
                        "Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            log.error("Contraseña incorrecta para usuario: {}", dto.getUsername());
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);
        log.info("Login exitoso para usuario: {}", dto.getUsername());

        return new TokenResponseDTO(
                token,
                usuario.getUsername(),
                usuario.getRol().name(),
                86400000L
        );
    }

    public boolean validarToken(String token) {
        log.info("Validando token");
        return jwtService.esTokenValido(token);
    }

    public String extraerUsername(String token) {
        return jwtService.extraerUsername(token);
    }

    public String extraerRol(String token) {
        return jwtService.extraerRol(token);
    }
    public List<Usuario> listarUsuarios() {
    return usuarioRepository.findAll();
    }
}