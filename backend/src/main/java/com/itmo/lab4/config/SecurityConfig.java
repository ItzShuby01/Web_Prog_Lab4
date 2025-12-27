package com.itmo.lab4.config;

import com.itmo.lab4.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow the Angular files & standard routes to bypass security
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/*.js", "/*.css", "/assets/**").permitAll()
                        .requestMatchers("/login", "/main", "/registration").permitAll()
                        // Allow Auth API
                        .requestMatchers("/api/auth/**").permitAll()
                        // Secure the rest
                        .anyRequest().authenticated()
                )
                // Enable Basic Auth but suppress the browser's native login dialog
                .httpBasic(basic -> basic
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Send 401 WITHOUT the "WWW-Authenticate" header
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                );

        return http.build();
    }
}