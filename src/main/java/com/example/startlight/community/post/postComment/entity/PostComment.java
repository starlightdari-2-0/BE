package com.example.startlight.community.post.postComment.entity;

import com.example.startlight.community.post.entity.Post;
import com.example.startlight.memory.memComment.entity.MemComment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @OneToMany(mappedBy = "parent")
    private List<PostComment> children = new ArrayList<>();

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long writer_id;

    @Column(nullable = false)
    private String writer_name;

    @LastModifiedDate
    private LocalDateTime createdAt;

    public void updateContent(String content) {
        this.content = content;
    }
}
