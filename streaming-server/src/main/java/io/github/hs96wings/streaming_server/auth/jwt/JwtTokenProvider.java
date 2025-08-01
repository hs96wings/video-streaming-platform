package io.github.hs96wings.streaming_server.auth.jwt;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(Member member) {
        long validityMillis = expiration * 60 * 1000L;
        return buildToken(member, validityMillis);
    }

    // 테스트용 헬퍼 메소드
    public String createExpiredToken(Member member) {
        long validityMillis = -60 * 1000L;
        return buildToken(member, validityMillis);
    }

    /**
     * 토큰 생성 로직을 담당하는 private 메소드
     * @param member 토큰에 담을 회원 정보
     * @param validityMillis 토큰의 유효 시간 (현재 시간 기준, ms)
     * @return 생성된 JWT 문자열
     */
    private String buildToken(Member member, long validityMillis) {
        Claims claims = Jwts.claims().setSubject(member.getUserid());
        claims.put("id", member.getId());
        claims.put("role", member.getRole());

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 테스트용 헬퍼 메소드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
