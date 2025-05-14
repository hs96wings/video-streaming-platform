package io.github.hs96wings.streaming_server.comment.controller;

import io.github.hs96wings.streaming_server.comment.dto.CommentResDto;
import io.github.hs96wings.streaming_server.comment.dto.CommentSaveReqDto;
import io.github.hs96wings.streaming_server.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 로그인한 사용자만 댓글 작성/삭제
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public CommentResDto add(@AuthenticationPrincipal UserDetails user, @RequestBody CommentSaveReqDto commentSaveReqDto) {
        String username = user.getUsername();
        return commentService.addComment(commentSaveReqDto, username);
    }

    // 누구나 댓글 조회 가능
    @GetMapping("/{videoId}")
    public List<CommentResDto> list(@PathVariable(name="videoId") Long videoId) {
        return commentService.getComments(videoId);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails user, @PathVariable(name="commentId") Long commentId) {
        commentService.deleteComment(commentId, user.getUsername());
        return ResponseEntity.ok().build();
    }
}
