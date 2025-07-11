package io.github.hs96wings.streaming_server.member.service;


import io.github.hs96wings.streaming_server.auth.dto.LoginRequestDto;
import io.github.hs96wings.streaming_server.auth.dto.SignupRequestDto;
import io.github.hs96wings.streaming_server.auth.service.AuthService;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService; // 테스트 대상

    @Test
    @DisplayName("회원 생성 성공")
    void testCreateMember_Success() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser", "1234");

        // repository의 동작을 미리 정의
        // "testUser"라는 아이디로 findByUserid를 호출하면 결과는 비어있음 (가입된 회원이 없다는 뜻)
        when(memberRepository.findByUserid("testUser")).thenReturn(Optional.empty());
        // 어떤 Member 객체든 save를 호출하면, 인자로 받은 Member 객체를 그대로 반환
        when(memberRepository.save(any(Member.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        // passwordEncoder의 동작을 미리 정의
        when(passwordEncoder.encode("1234")).thenReturn("encoded_password");

        // when (실제 테스트 실행)
        // 테스트 대상인 authService.create를 진짜로 호출
        Member savedMember = authService.create(signupRequestDto);

        // then
        assertNotNull(savedMember);
        assertEquals("testUser", savedMember.getUserid());
        assertEquals("encoded_password", savedMember.getPassword());

        // memberRepository의 findByUserid가 "testUser" 인자와 함께 1번 호출되었는지 검증
        verify(memberRepository, times(1)).findByUserid("testUser");
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(passwordEncoder, times(1)).encode("1234");
    }

    @Test
    @DisplayName("회원 생성 실패 - 중복된 아이디")
    void testCreateMember_Fail_DuplicateId() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser", "1234");

        // testUser라는 아이디로 findByUserid를 호출하면, 이미 가입된 Member 객체 리턴
        when(memberRepository.findByUserid("testUser")).thenReturn(Optional.of(new Member()));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.create(signupRequestDto);
        });

        verify(memberRepository, times(1)).findByUserid("testUser");
    }

    @Test
    @DisplayName("로그인 성공")
    void testLoginMember_Success() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "1234");
        Member member = Member.builder().id(1L).userid("testUser").password("encoded_password").build();

        when(memberRepository.findByUserid("testUser")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(anyString(), eq("encoded_password"))).thenReturn(true);

        // when
        Member savedMember = authService.login(loginRequestDto);

        // then
        assertEquals("testUser", savedMember.getUserid());

        // memberRepository의 findByUserid가 "testUser" 인자와 함께 1번 호출되었는지 검증
        verify(memberRepository, times(1)).findByUserid("testUser");
        verify(passwordEncoder, times(1)).matches("1234", "encoded_password");
    }

    @Test
    @DisplayName("로그인 실패 - 사용자가 존재하지 않음")
    void testLoginMember_Fail_UserNotFound() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "1234");
        // 가입된 유저가 없으므로 empty 리턴
        when(memberRepository.findByUserid("testUser")).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authService.login(loginRequestDto);
        });

        assertEquals("존재하지 않는 아이디입니다", exception.getMessage());
        verify(memberRepository, times(1)).findByUserid("testUser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 일치하지 않음")
    void testLoginMember_Fail_NotMatchPassword() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "1234");
        Member member = Member.builder().id(1L).userid("testUser").password("encoded_password").build();

        // userid는 성공적으로 조회
        when(memberRepository.findByUserid("testUser")).thenReturn(Optional.of(member));
        // 비밀번호 비교 결과는 false
        when(passwordEncoder.matches(anyString(), eq("encoded_password"))).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequestDto);
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(memberRepository, times(1)).findByUserid("testUser");
        verify(passwordEncoder, times(1)).matches("1234", "encoded_password");
    }
}
