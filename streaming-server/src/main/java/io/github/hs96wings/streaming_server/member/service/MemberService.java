package io.github.hs96wings.streaming_server.member.service;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.dto.MemberLoginReqDto;
import io.github.hs96wings.streaming_server.member.dto.MemberSaveReqDto;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberSaveReqDto memberSaveReqDto) {
        // 이미 가입되어 있는 아이디 검증
        if (memberRepository.findByUserid(memberSaveReqDto.getUserid()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member newMember = Member.builder()
                .userid(memberSaveReqDto.getUserid())
                .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
                .build();
        return memberRepository.save(newMember);
    }

    public Member login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.findByUserid(memberLoginReqDto.getUserid()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다"));

        if (!passwordEncoder.matches(memberLoginReqDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
