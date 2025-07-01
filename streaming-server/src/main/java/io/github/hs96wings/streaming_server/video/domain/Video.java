package io.github.hs96wings.streaming_server.video.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.github.hs96wings.streaming_server.comment.domain.Comment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Lob
    private String description;
    private String videoPath;
    private String thumbnailPath;
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VideoStatus videoStatus = VideoStatus.UPLOADED;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "video", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
