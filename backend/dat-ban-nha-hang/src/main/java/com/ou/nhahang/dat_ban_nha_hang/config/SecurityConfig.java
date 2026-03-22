package com.ou.nhahang.dat_ban_nha_hang.config;

import com.ou.nhahang.dat_ban_nha_hang.security.JwtAuthenticationFilter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Chỉ đích danh tên miền của React được phép gọi vào (Dùng * nếu muốn mở cho
        // tất cả, nhưng không an toàn)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // 2. Cho phép các Method HTTP nào? (Phải có OPTIONS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Cho phép Frontend gửi lên những Header nào? (Bắt buộc phải có
        // Authorization để gửi Token)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 4. (Tùy chọn) Cho phép gửi Cookie chéo miền nếu có dùng
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng luật CORS này cho TOÀN BỘ mọi endpoint trong hệ thống
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // .csrf(AbstractHttpConfigurer::disable)
                // .cors(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Này cho phép đăng nhập, đăng ký
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants/*/reviews").authenticated()
                        .anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
