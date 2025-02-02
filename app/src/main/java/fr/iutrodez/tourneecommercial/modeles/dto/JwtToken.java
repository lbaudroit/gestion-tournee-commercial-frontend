package fr.iutrodez.tourneecommercial.modeles.dto;

public class JwtToken {
    private String token;
    private long expiration;

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
