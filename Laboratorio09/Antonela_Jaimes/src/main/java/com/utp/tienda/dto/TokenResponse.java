package com.utp.tienda.dto;

import java.util.List;

public class TokenResponse {

    private String token;
    private String tipo = "Bearer";
    private String username;
    private List<String> roles;
    private long expiracionEnMilisegundos;

    public TokenResponse() {
    }

    public TokenResponse(String token, String username, List<String> roles, long expiracionEnMilisegundos) {
        this.token = token;
        this.tipo = "Bearer";
        this.username = username;
        this.roles = roles;
        this.expiracionEnMilisegundos = expiracionEnMilisegundos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public long getExpiracionEnMilisegundos() {
        return expiracionEnMilisegundos;
    }

    public void setExpiracionEnMilisegundos(long expiracionEnMilisegundos) {
        this.expiracionEnMilisegundos = expiracionEnMilisegundos;
    }
}
