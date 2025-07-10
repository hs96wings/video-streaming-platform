package io.github.hs96wings.streaming_server.auth.jwt;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String jwt = header.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                Long id = claims.get("id", Long.class);
                String username = claims.getSubject();
                String roleStr = claims.get("role", String.class); // JWT에서 role 문자열 추출
                Role role = Role.valueOf(roleStr);  // 문자열을 Role Enum으로 변환

                // DB 조회 없이, JWT 정보로 Member 객체(Principal) 생성
                // UserDetails를 구현했으므로 Member가 Principal이 될 수 있음
                Member member = Member.builder()
                        .id(id)
                        .userid(username)
                        .role(role)
                        .build();
                // Authentication 객체 생성 (Principal 자리에 Member 객체를 직접 넣음)
                // member.getAuthorities()는 Member 클래스에 이미 구현된 메서드를 활용
                Authentication auth = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // 토큰이 없거나 잘못됐어도, 그냥 계속 chain 을 탄다
        chain.doFilter(request, response);
    }
}
