package com.kahlab.easytask.security;

import java.time.Duration;
import java.time.LocalDateTime;


public class RefreshTokenData {

    private final String email;
    private final LocalDateTime expiresAt;

    public RefreshTokenData(String email, Duration duration) {
        this.email = email;
        this.expiresAt = LocalDateTime.now().plus(duration);
    }

    public String getEmail() {
        return email;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
