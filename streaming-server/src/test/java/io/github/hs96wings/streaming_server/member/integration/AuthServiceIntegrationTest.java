package io.github.hs96wings.streaming_server.member.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.auth.jwt.JwtTokenProvider;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.auth.dto.LoginRequestDto;
import io.github.hs96wings.streaming_server.auth.dto.SignupRequestDto;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import io.github.hs96wings.streaming_server.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthServiceIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AuthService authService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    private SignupRequestDto dto;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceIntegrationTest.class);

    @BeforeEach
    void setUp() {
        dto = new SignupRequestDto("testUser", "1234");
    }

    @Test
    @Transactional
    @DisplayName("DB에 user를 저장한다")
    void createMember_shouldPersist() {
        // given
        Member newMember = authService.create(dto);

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
        Member member = Member.builder()
                .userid("testUser")
                .password(passwordEncoder.encode("1234"))
                .build();
        memberRepository.save(member);

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.create(dto)
        );
        assertThat(ex.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");

        // and then: DB에 중복 없이 하나만 존재
        List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).hasSize(1)
                .extracting(Member::getUserid)
                .containsExactly("testUser");
    }

    @Test
    @DisplayName("통합 로그인 테스트: 실제 JWT를 발급받는다")
    void loginIntegration() throws Exception {
        // given
        authService.create(dto);
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "1234");

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        log.debug(json);
    }

    @Test
    @DisplayName("토큰 유효성 검사")
    void validateToken_successHaveToken() throws Exception {
        // given
        Member member = Member.builder()
                .userid("testUser")
                .password(passwordEncoder.encode("1234"))
                .build();
        memberRepository.save(member);
        String token = jwtTokenProvider.createToken(member);

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
