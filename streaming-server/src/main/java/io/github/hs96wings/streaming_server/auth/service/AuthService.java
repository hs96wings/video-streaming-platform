package io.github.hs96wings.streaming_server.auth.service;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.auth.dto.LoginRequestDto;
import io.github.hs96wings.streaming_server.auth.dto.SignupRequestDto;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(SignupRequestDto signupRequestDto) {
        // 이미 가입되어 있는 아이디 검증
        if (memberRepository.findByUserid(signupRequestDto.getUserid()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member newMember = Member.builder()
                .userid(signupRequestDto.getUserid())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build();
        return memberRepository.save(newMember);
    }

    public Member login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByUserid(loginRequestDto.getUserid()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 아이디입니다"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
