package com.team.backend.config.security.dto;

public record JwtResponseDto(
        String username,
        Long id,
        String token,
        String profilePhotoUrl
) {
}