package com.kahlab.easytask.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/collaborators/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/collaborators/register").permitAll()
                        .requestMatchers("/collaborators/refresh-token").permitAll()

                        // COLLABORATORS
                        .requestMatchers(HttpMethod.GET, "/collaborators/**").hasRole("SUPERIOR")
                        .requestMatchers(HttpMethod.POST, "/collaborators/**").hasRole("SUPERIOR")
                        .requestMatchers(HttpMethod.PUT, "/collaborators/**").hasRole("SUPERIOR")
                        .requestMatchers(HttpMethod.DELETE, "/collaborators/**").hasRole("SUPERIOR")

                        // CLIENTS
                        .requestMatchers(HttpMethod.GET, "/clients/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/clients/**").hasRole("SUPERIOR")
                        .requestMatchers(HttpMethod.PUT, "/clients/**").hasRole("SUPERIOR")
                        .requestMatchers(HttpMethod.DELETE, "/clients/**").hasRole("SUPERIOR")

                        // TASKS
                        .requestMatchers(HttpMethod.GET, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("SUPERIOR")

                        // BOARDS
                        .requestMatchers(HttpMethod.GET, "/boards/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/boards/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/boards/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/boards/**").hasRole("SUPERIOR")

                        // PHASES
                        .requestMatchers(HttpMethod.GET, "/phases/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/phases/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/phases/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/phases/**").hasRole("SUPERIOR")

                        // RELATÓRIOS
                        .requestMatchers("/reports/**").authenticated()

                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
