package com.utp.API.CRUD.dto;

public record TokenResponse(
    String tokenType,
    String accessToken,
    Long expiresInSeconds
) {}