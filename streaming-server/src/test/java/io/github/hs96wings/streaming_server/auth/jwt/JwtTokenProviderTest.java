package io.github.hs96wings.streaming_server.auth.jwt;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private final String rawSecret = "my-test-secret-key-my-test-secret-key-my-test-secret-key";
    private final String encodedSecret = Base64.getEncoder().encodeToString(rawSecret.getBytes());
    private final int expirationInMinutes = 10;
    private Member member;

    @BeforeEach
    void setup() {
        jwtTokenProvider = new JwtTokenProvider(encodedSecret, expirationInMinutes);
        member = Member.builder()
            .id(1L)
            .userid("testuser")
            .role(Role.USER)
            .build();
    }

    @Test
    @DisplayName("JWT 생성 시, 사용자 ID, 역할, 내부 ID가 정확히 Claims에 포함되어야 한다")
    void createToken_shouldContainCorrectClaims() {
        // given

        // when
        String token = jwtTokenProvider.createToken(member);

        // then
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(encodedSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("id", Integer.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        assertThat(expiration.getTime() - issuedAt.getTime()).isBetween(9 * 60 * 1000L, 10 * 60 * 1000L); // 9 ~ 10분 범위
    }

    @Test
    @DisplayName("유효한 토큰일 경우 validateToken()은 true를 반환해야 한다")
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        String token = jwtTokenProvider.createToken(member);

        boolean result = jwtTokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("JWT 토큰이 만료된 경우 validateToken()은 false를 반환해야 한다")
    void validateToken_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        // 만료 시간 = 1초
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(encodedSecret, 0); // 0분 = 즉시 만료

        String token = shortLivedProvider.createToken(member);

        Thread.sleep(1000); // 1초 대기

        boolean result = jwtTokenProvider.validateToken(token);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 시크릿 키로 서명된 JWT는 validateToken()에서 false를 반환해야 한다")
    void validateToken_shouldReturnFalse_whenSecretKeyIsInvalid() {
        String token = jwtTokenProvider.createToken(member);

        // 다른 키로 검증 시도
        String wrongSecret = Base64.getEncoder().encodeToString("wrong-secret-wrong-secret-wrong-secret-wrong-secret".getBytes());
        JwtTokenProvider wrongProvider = new JwtTokenProvider(wrongSecret, expirationInMinutes);

        boolean result = wrongProvider.validateToken(token);
        assertThat(result).isFalse();
    }
}
