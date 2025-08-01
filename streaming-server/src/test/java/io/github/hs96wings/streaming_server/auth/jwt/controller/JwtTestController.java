package io.github.hs96wings.streaming_server.auth.jwt.controller;

import io.github.hs96wings.streaming_server.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class JwtTestController {
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return ResponseEntity.ok(member.getUserid());
    }
}
