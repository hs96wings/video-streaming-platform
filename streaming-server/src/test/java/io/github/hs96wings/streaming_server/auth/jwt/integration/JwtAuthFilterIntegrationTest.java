package io.github.hs96wings.streaming_server.auth.jwt.integration;

import io.github.hs96wings.streaming_server.auth.jwt.controller.JwtTestController;
import io.github.hs96wings.streaming_server.common.configs.SecurityConfigs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JwtTestController.class)
@Import(SecurityConfigs.class)
@TestPropertySource(properties = {
        "jwt.secretKey=" + "#{T(java.util.Base64).getEncoder().encodeToString('8ce9kBVFe+7IoIlf/7izMI0PAsXKpjbC+tVsWEwGVr2lmrE1UHiKffdrOg/Bo2tX'.getBytes())}"
})
public class JwtAuthFilterIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private String secretKey = Base64.getEncoder().encodeToString("8ce9kBVFe+7IoIlf/7izMI0PAsXKpjbC+tVsWEwGVr2lmrE1UHiKffdrOg/Bo2tX".getBytes());

    private String createToken() {
        return Jwts.builder()
                .setSubject("testuser")
                .claim("id", 1L)
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName()))
                .compact();
    }

    @Test
    @DisplayName("유효한 JWT가 있을 경우 /me 요청은 사용자 ID를 반환한다")
    void validJwt_returnsUserId() throws Exception {
        String token = createToken();

        mockMvc.perform(get("/api/test/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("testuser"));
    }

    @Test
    @DisplayName("잘못된 JWT일 경우 /me 요청은 401 Unauthorized를 반환한다")
    void invalidjwt_returnsUnauthorized() throws Exception {
        String invalidToken = "Bearer invalid.token.value";

        mockMvc.perform(get("/api/test/me")
                .header(HttpHeaders.AUTHORIZATION, invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid or expired token"));
    }
}
