package io.github.hs96wings.streaming_server.auth.controller;

import io.github.hs96wings.streaming_server.auth.jwt.JwtTokenProvider;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.auth.dto.LoginRequestDto;
import io.github.hs96wings.streaming_server.auth.dto.SignupRequestDto;
import io.github.hs96wings.streaming_server.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> memberCreate(@RequestBody SignupRequestDto signupRequestDto) {
        Member member = authService.create(signupRequestDto);

        Map<String, Object> loginInfo = getLoginInfo(member);

        return new ResponseEntity<>(loginInfo, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDto loginRequestDto) {
        Member member = authService.login(loginRequestDto);

        Map<String, Object> loginInfo = getLoginInfo(member);

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken() {
        // JwtAuthenticationFilter에서 토큰 유효성 검사되므로 여기까지 오면 유효한 것
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> getLoginInfo(Member member) {
        String jwtToken = jwtTokenProvider.createToken(member);
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return loginInfo;
    }
}
