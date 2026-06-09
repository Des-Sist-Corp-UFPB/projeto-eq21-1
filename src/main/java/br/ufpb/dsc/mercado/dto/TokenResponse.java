package br.ufpb.dsc.mercado.dto;

public record TokenResponse(String token, String tipo, String email, String role) {

    public static TokenResponse bearer(String token, String email, String role) {
        return new TokenResponse(token, "Bearer", email, role);
    }
}
