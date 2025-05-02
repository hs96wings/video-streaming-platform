package io.github.hs96wings.streaming_server.member.integration;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.dto.MemberSaveReqDto;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import io.github.hs96wings.streaming_server.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberServiceIntegrationTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    private MemberSaveReqDto dto;

    @BeforeEach
    void setUp() {
        dto = new MemberSaveReqDto("testUser", "1234");
    }

    @Test
    @Transactional
    @DisplayName("DB에 user를 저장한다")
    void createMember_shouldPersist() {
        // given
        Member newMember = memberService.create(dto);

        // when & then
        assertThat(newMember.getId()).isNotNull();
        assertThat(memberRepository.findByUserid("testUser")).isPresent();

        // and then: DB에 존재
        List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).hasSize(1)
                .extracting(Member::getUserid)
                .containsExactly("testUser");
    }

    @Test
    @Transactional
    @DisplayName("동일한 userid로 회원 생성 시 예외가 발생하고, DB에는 단일 레코드만 남아야 한다")
    void createDuplicateMember_throwsIllegalArgument() {
        // given
        memberService.create(dto);

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> memberService.create(dto)
        );
        assertThat(ex.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");

        // and then: DB에 중복 없이 하나만 존재
        List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).hasSize(1)
                .extracting(Member::getUserid)
                .containsExactly("testUser");
    }
}
