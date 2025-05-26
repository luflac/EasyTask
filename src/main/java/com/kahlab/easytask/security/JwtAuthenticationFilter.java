package com.kahlab.easytask.security;

import com.kahlab.easytask.repository.CollaboratorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlackList tokenBlackList;
    private final CollaboratorRepository collaboratorRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   TokenBlackList tokenBlackList,
                                   CollaboratorRepository collaboratorRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenBlackList = tokenBlackList;
        this.collaboratorRepository = collaboratorRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignora o filtro para rotas públicas
        if (path.contains("/refresh-token") || path.contains("/login") || path.contains("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenBlackList.isTokenRevoked(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token revogado. Faça login novamente.");
                return;
            }

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                String accessLevel = jwtUtil.extractAccessLevel(token);

                // 🚨 Verifica se o colaborador ainda existe
                var optionalUser = collaboratorRepository.findByEmail(email);
                if (optionalUser.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Colaborador não encontrado. Token inválido.");
                    return;
                }

                // ✅ Se tudo certo, autentica
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessLevel))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}