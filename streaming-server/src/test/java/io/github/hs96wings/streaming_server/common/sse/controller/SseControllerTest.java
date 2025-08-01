package io.github.hs96wings.streaming_server.common.sse.controller;

import io.github.hs96wings.streaming_server.common.configs.SecurityConfigs;
import io.github.hs96wings.streaming_server.common.sse.service.SseEmitterService;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SseController.class)
@Import(SecurityConfigs.class)
public class SseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SseEmitterService sseEmitterService;

    @MockitoBean
    private MemberRepository memberRepository;

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("connect 호출 시 SseEmitter가 반환되어야 한다")
    void connect_shouldReturnSseEmitter() throws Exception {
        // given
        Long memberId = 1L;
        SseEmitter sseEmitter = new SseEmitter();
        given(memberRepository.findByUserid("testUser")).willReturn(Optional.of(Member.builder().id(memberId).userid("testuser").build()));
        given(sseEmitterService.connect(memberId)).willReturn(sseEmitter);

        // when & then
        mockMvc.perform(get("/api/sse/connect"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "notFound")
    @DisplayName("없는 사용자로 connect 요청 시 404 예외 발생")
    void connect_shouldThrow_ifUserNotFound() throws Exception {
        given(memberRepository.findByUserid("notFound")).willReturn(Optional.empty());

        mockMvc.perform(get("/api/sse/connect"))
                .andExpect(status().isNotFound());
    }
}
