package io.github.hs96wings.streaming_server.common.auth;

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

    private final RequestMatcher protectedMatcher;

    public JwtAuthFilter() {
        List<RequestMatcher> matchers = List.of(
                new AntPathRequestMatcher("/api/video/**", "POST"),
                new AntPathRequestMatcher("/api/video/**", "PUT"),
                new AntPathRequestMatcher("/api/video/**", "DELETE"),
                new AntPathRequestMatcher("/api/comment/**", "POST"),
                new AntPathRequestMatcher("/api/comment/**", "DELETE")
                // 추가가 필요할 때마다 이 리스트에만 넣으면 OK
        );
        this.protectedMatcher = new OrRequestMatcher(matchers);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (protectedMatcher.matches(request)) {
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("Bearer 형식이 아닙니다");
                return;
            }

            try {
                String jwtToken = token.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // 나머지는 그냥 통과
        chain.doFilter(request, response);
    }
}
