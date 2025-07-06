package io.github.hs96wings.streaming_server.common.configs;

import io.github.hs96wings.streaming_server.auth.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 메서드 보안 활성화
public class SecurityConfigs {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 요청 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        // 공용 GET API
                        .requestMatchers(HttpMethod.GET, "/api/video/**", "/api/comment/**").permitAll()
                        // 로그 수집용은 누구나 가능
                        .requestMatchers(HttpMethod.POST, "/api/log/**").permitAll()
                        // 임시: 관리자 영상 수정 권한 (추후 수정 필요)
                        .requestMatchers(HttpMethod.PATCH, "/api/video/**").permitAll()
                        // SSE 연결은 인증 없이 열어둠
                        .requestMatchers("/api/sse/**").permitAll()
                        // 로그인 및 회원가입 API
                        .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                        // /api/admin은 관리자 권한 필요
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 나머지 /api/**은 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 정적 리소스 등 나머지는 그대로 허용
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080", "http://43.200.192.246", "http://lwasky.site", "https://lwasky.site"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
