package io.github.hs96wings.streaming_server.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.common.auth.JwtTokenProvider;
import io.github.hs96wings.streaming_server.common.configs.SecurityConfigs;
import io.github.hs96wings.streaming_server.member.controller.MemberController;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.dto.MemberLoginReqDto;
import io.github.hs96wings.streaming_server.member.dto.MemberSaveReqDto;
import io.github.hs96wings.streaming_server.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@Import(SecurityConfigs.class) // permitAll 규칙을 가져옴
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider; // 실제 필터는 무시

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 생성 성공")
    void testMemberCreate() throws Exception {
        // given
        MemberSaveReqDto reqDto = new MemberSaveReqDto("testUser", "1234");

        Member savedMember = Member.builder()
                .id(1L)
                .userid("testUser")
                .password("1234")
                .build();

        when(memberService.create(any(MemberSaveReqDto.class))).thenReturn(savedMember);

        // when & then
        mockMvc.perform(post("/member/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reqDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("중복 회원 처리 테스트")
    void testDuplicateMember() throws Exception {
        // given
        MemberSaveReqDto duplicateDto = new MemberSaveReqDto("testUser", "1234");

        // 중복 상황을 시뮬레이션 - create가 예외를 던지도록 설정
        when(memberService.create(any(MemberSaveReqDto.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 아이디입니다."));


        // when & then
        mockMvc.perform(post("/member/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("이미 존재하는 아이디입니다.")));

    }

    @Test
    @DisplayName("로그인 성공 시 id와 토큰이 응답에 들어간다")
    void testLoginMember() throws Exception {
        // given
        MemberLoginReqDto memberLoginReqDto = new MemberLoginReqDto("testUser", "1234");
        Member savedMember = Member.builder()
                .id(1L)
                .userid("testUser")
                .password("1234")
                .build();

        when(memberService.login(any(MemberLoginReqDto.class))).thenReturn(savedMember);
        when(jwtTokenProvider.createToken("testUser", "USER")).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/member/doLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(memberLoginReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
    }
}
