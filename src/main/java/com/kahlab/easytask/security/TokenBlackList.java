package com.kahlab.easytask.security;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlackList {

    private final Set<String> blacklist = new HashSet<>();

    public void revokeToken(String token) {
        blacklist.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return blacklist.contains(token);
    }
}
