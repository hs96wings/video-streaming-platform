package io.github.hs96wings.streaming_server.member.repository;

import io.github.hs96wings.streaming_server.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryTest.class);

    @BeforeEach
    void setup() {
        member = Member.builder().userid("testUser").password("1234").build();
    }

    @Test
    @DisplayName("회원이 DB에 잘 저장되는지 확인")
    void saveMember() {
        // given
        // static member 사용

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isNotNull();
        // JPA에서 save()한 객체는 영속성 컨텍스트 안에서 할당되기 때문에, member.getId()는 여전히 null일 수도 있다
        assertThat(savedMember.getId()).isNotNull(); // id가 정상적으로 생성되었는지만 확인
        assertThat(savedMember.getUserid()).isEqualTo(member.getUserid());
        assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(savedMember.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("회원이 userid로 검색되는지 확인")
    void findByUserid() {
        // given
        // static member 사용
        String userid = "testUser";
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isNotNull();
        assertThat(findMember.getUserid()).isEqualTo(member.getUserid());
        assertThat(findMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(findMember.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("없는 회원을 검색하면 오류가 뜬다")
    void findByUserid_fail_UserIsEmpty() {
        // given
        String userid = "testUser";

        // when
        Optional<Member> findMember = memberRepository.findByUserid(userid);

        // then
        assertThat(findMember).isEmpty();
    }

    @Test
    @DisplayName("중복된 userid 저장 시 제약 조건에 의해 예외 발생")
    void saveMember_fail_DuplicateUserid() {
        // given
        Member duplicateMember = Member.builder().userid("testUser").password("5678").build();
        memberRepository.save(member);

        // when & then
        assertThatThrownBy(() -> memberRepository.saveAndFlush(duplicateMember))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
