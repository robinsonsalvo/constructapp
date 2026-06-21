package com.constructapp.ms_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDTO {
    private String token;
    private String username;
    private String rol;
    private Long expiresIn;
}