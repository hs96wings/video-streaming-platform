package io.github.hs96wings.streaming_server.auth.service;

import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.domain.Role;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("회원 아이디로 사용자 정보를 정상 조회할 수 있어야 한다")
    void loadUserByUserId_success() {
        Member member = Member.builder()
                .id(1L)
                .userid("testuser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        given(memberRepository.findByUserid("testuser")).willReturn(Optional.of(member));

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 UsernameNotFoundException이 발생해야 한다")
    void loadUserByUsername_notFound() {
        given(memberRepository.findByUserid("notFound")).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("notFound");
        });
    }
}
