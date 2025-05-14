package io.github.hs96wings.streaming_server.member.controller;

import io.github.hs96wings.streaming_server.common.auth.JwtTokenProvider;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.dto.MemberLoginReqDto;
import io.github.hs96wings.streaming_server.member.dto.MemberSaveReqDto;
import io.github.hs96wings.streaming_server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        Member member = memberService.create(memberSaveReqDto);

        Map<String, Object> loginInfo = getLoginInfo(member);

        return new ResponseEntity<>(loginInfo, HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto) {
        Member member = memberService.login(memberLoginReqDto);

        Map<String, Object> loginInfo = getLoginInfo(member);

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    private Map<String, Object> getLoginInfo(Member member) {
        String jwtToken = jwtTokenProvider.createToken(member.getUserid(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return loginInfo;
    }
}
