package com.kahlab.easytask.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    // Armazena: refreshToken → dados (email + validade)
    private final Map<String, RefreshTokenData> refreshTokens = new ConcurrentHashMap<>();

    public void store(String refreshToken, String email, Duration duration) {
        refreshTokens.put(refreshToken, new RefreshTokenData(email, duration));
    }

    public boolean isValid(String refreshToken) {
        RefreshTokenData data = refreshTokens.get(refreshToken);
        return data != null && !data.isExpired();
    }

    public String getEmail(String refreshToken) {
        RefreshTokenData data = refreshTokens.get(refreshToken);
        return data != null ? data.getEmail() : null;
    }

    public void revoke(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }
}
