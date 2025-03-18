package fr.iutrodez.tourneecommercial.model.dto;

public class JwtToken {
    private final String token;
    private final long expiration;

    public JwtToken(String token, long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public long getExpiration() {
        return expiration;
    }
}
