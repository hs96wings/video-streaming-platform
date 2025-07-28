package io.github.hs96wings.streaming_server.common.configs;

import io.github.hs96wings.streaming_server.auth.jwt.JwtTokenProvider;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member member;
    private Member admin;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .userid("testuser")
                .password("1234")
                .role(Role.USER)
                .build();
        admin = Member.builder()
                .userid("admin")
                .password("1234")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    @DisplayName("토큰을 가지고 접근했을 때 200 반환")
    void access_withValidToken_shouldReturn200() throws Exception {
        // given
        String token = jwtTokenProvider.createToken(member);

        // when
        mockMvc.perform(get("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 없이 접근했을 때 403 반환")
    void access_withoutToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CORS 설정 테스트 - 허용된 Origin")
    void cors_withAllowedOrigin_shouldReturnOKHeaders() throws Exception {
        mockMvc.perform(options("/api/video/1")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("CORS 설정 테스트 - 허용되지 않은 Origin")
    void cors_withDisallowedOrigin_shouldNotReturnCORSHeader() throws Exception {
        mockMvc.perform(options("/api/video/1")
                .header(HttpHeaders.ORIGIN, "http://test.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("PreAuthorize - Admin일 경우 200 반환")
    void access_withAuthorizeAdmin_shouldReturn200() throws Exception {
        // given
        String token = jwtTokenProvider.createToken(admin);

        mockMvc.perform(get("/api/admin/stats/visits/daily")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PreAuthorize - Admin이 아닐 경우 403 반환")
    void access_withoutAuthorizeAdmin_shouldReturn403() throws Exception {
        // given
        String token = jwtTokenProvider.createToken(member); // 또는 토큰 없이

        mockMvc.perform(get("/api/admin/stats/visits/daily")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("JWT 시그니처가 위조된 경우 401 반환")
    void access_withTamperedToken_shouldReturn401() throws Exception {
        // given
        String token = jwtTokenProvider.createToken(member);
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".WRONG_SIGNATURE";

        // when & then
        mockMvc.perform(get("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JWT 토큰이 만료된 경우 401 반환")
    void access_withExpiredToken_shouldReturn401() throws Exception {
        // given
        String expiredToken = jwtTokenProvider.createExpiredToken(member);

        // when & then
        mockMvc.perform(get("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
