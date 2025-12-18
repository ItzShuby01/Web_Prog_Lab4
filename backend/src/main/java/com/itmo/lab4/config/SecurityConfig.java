package com.itmo.lab4.config;

import com.itmo.lab4.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    //Password Encoder ("пароль должен храниться в виде хэш-суммы")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Authentication Manager (to process the login attempt)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        // Use the AuthenticationManagerBuilder to configure the details
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        // Return the final built object
        return authenticationManagerBuilder.build();
    }
    //Security Filter Chain (Defines access rules)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Since Angular runs on different port (4200) -> need CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF's not needed for stateless REST APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Stateless session policy = no session cookies, only tokens/basic auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow anyone to access /api/auth/** (login and registration)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Require authentication for all other endpoints (e.g., /api/area/**)
                        .anyRequest().authenticated()
                );


        // Enables HTTP Basic authentication
        http.httpBasic(httpBasic -> {});

        return http.build();
    }

    // CORS Configuration (Allows Angular to talk to Spring)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Angular dev server runs on port 4200 / 8000
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}