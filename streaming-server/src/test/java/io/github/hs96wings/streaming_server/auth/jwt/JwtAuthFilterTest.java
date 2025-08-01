package io.github.hs96wings.streaming_server.auth.jwt;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

public class JwtAuthFilterTest {
    private JwtAuthFilter jwtAuthFilter;
    private String secretKey = Base64.getEncoder().encodeToString("QjKfj/pTjp+clbhTJutqoIAfaevEEIoO/oQHWPLWGAsz6+aLZ2W5ReZaw9YKKT1Y".getBytes());

    @BeforeEach
    void setup() throws Exception {
        jwtAuthFilter = new JwtAuthFilter();

        // private 필드 강제 주입
        Field secretKeyField = JwtAuthFilter.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtAuthFilter, secretKey);
    }

    private String createToken(Long id, String username, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 60000);
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName()))
                .compact();
    }

    @Test
    @DisplayName("유효한 JWT가 주어졌을 때 SecurityContext에 인증 정보가 정상적으로 저장되어야 한다")
    void validJwt_shouldSetAuthenticationInSecurityContext() throws Exception {
        // given
        String token = createToken(1L, "testuser", Role.USER);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isInstanceOf(Member.class);

        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(member.getUserid()).isEqualTo("testuser");
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("잘못된 JWT가 전달되면 401 Unauthorized 응답을 반환해야 한다")
    void invalidJwt_shouldReturnUnauthorized() throws Exception {
        // given
        String invalidToken = "Bearer invalid.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", invalidToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = Mockito.mock(MockFilterChain.class);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Invalid or expired token");
    }
}
