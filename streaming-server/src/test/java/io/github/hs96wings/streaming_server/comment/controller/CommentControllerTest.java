package io.github.hs96wings.streaming_server.comment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.Comment.controller.CommentController;
import io.github.hs96wings.streaming_server.Comment.domain.Comment;
import io.github.hs96wings.streaming_server.Comment.dto.CommentResDto;
import io.github.hs96wings.streaming_server.Comment.dto.CommentSaveReqDto;
import io.github.hs96wings.streaming_server.Comment.service.CommentService;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    private CommentSaveReqDto commentSaveReqDto;
    private CommentResDto commentResDto;

    private static final Logger log = LoggerFactory.getLogger(CommentControllerTest.class);

    @BeforeEach
    void setUp() {
        commentSaveReqDto = new CommentSaveReqDto(1L, "테스트 댓글");

        commentResDto = new CommentResDto();
        commentResDto.setId(1L);
        commentResDto.setContent("테스트 댓글");
        commentResDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("댓글 작성 요청 시 200 전달")
    @WithMockUser(username = "testUser", roles={"USER"})
    void publishComment_returnOk() throws Exception {
        // when
        when(commentService.addComment(any(CommentSaveReqDto.class), anyString())).thenReturn(commentResDto);

        // then
        mockMvc.perform(post("/api/comment")
                // WithMockUser 또는
                // .with(user("testUser").roles("USER")) 도 가능
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentSaveReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("테스트 댓글"))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 삭제 요청 시 200 전달")
    @WithMockUser(username = "testUser", roles={"USER"})
    void deleteComment_returnOk() throws Exception {
        // when
        // 불필요한 stub: 기본적으로 void 메서드는 아무 동작없이 넘어가도록 설정되어 있음
        // doNothing().when(commentService).deleteComment(anyLong(), anyString());

        // then
        mockMvc.perform(delete("/api/comment/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // and then:
        verify(commentService).deleteComment(1L, "testUser");
    }
}
