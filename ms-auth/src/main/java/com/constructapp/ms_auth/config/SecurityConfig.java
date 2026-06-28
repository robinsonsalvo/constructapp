package com.constructapp.ms_auth.config;

<<<<<<< HEAD
=======
import com.constructapp.ms_auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
<<<<<<< HEAD

@Configuration
@EnableWebSecurity
public class SecurityConfig {

=======
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
<<<<<<< HEAD
                .requestMatchers(
                    "/api/auth/**",
=======
                // Solo login y register son publicos
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // Swagger siempre publico
                .requestMatchers(
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).permitAll()
<<<<<<< HEAD
                .anyRequest().authenticated()
            );
=======
                // validate y listar requieren token
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
