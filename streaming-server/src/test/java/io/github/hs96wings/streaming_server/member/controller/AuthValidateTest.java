package io.github.hs96wings.streaming_server.member.controller;

import io.github.hs96wings.streaming_server.auth.controller.AuthController;
import io.github.hs96wings.streaming_server.auth.jwt.JwtTokenProvider;
import io.github.hs96wings.streaming_server.auth.service.AuthService;
import io.github.hs96wings.streaming_server.common.configs.SecurityConfigs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfigs.class)
public class AuthValidateTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username="testUser", roles = "USER") // SpringSecurity에서 인증된 사용자로 설정
    @DisplayName("토큰 유효성 검사")
    void testValidateToken() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk());
    }
}
