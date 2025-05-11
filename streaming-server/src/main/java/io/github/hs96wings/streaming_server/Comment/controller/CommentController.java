package io.github.hs96wings.streaming_server.Comment.controller;

import io.github.hs96wings.streaming_server.Comment.dto.CommentResDto;
import io.github.hs96wings.streaming_server.Comment.dto.CommentSaveReqDto;
import io.github.hs96wings.streaming_server.Comment.service.CommentService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public CommentResDto add(@AuthenticationPrincipal UserDetails user, @RequestBody CommentSaveReqDto commentSaveReqDto) {
        String username = user.getUsername();
        return commentService.addComment(commentSaveReqDto, username);
    }

    @GetMapping("/{videoId}")
    public List<CommentResDto> list(@PathVariable(name="videoId") Long videoId) {
        return commentService.getComments(videoId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails user, @PathVariable(name="commentId") Long commentId) {
        commentService.deleteComment(commentId, user.getUsername());
        return ResponseEntity.ok().build();
    }
}
