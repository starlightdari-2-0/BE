package com.example.startlight.postComment.entity;

import com.example.startlight.member.entity.Member;
import com.example.startlight.post.dao.PostDao;
import com.example.startlight.post.entity.Post;
import com.example.startlight.postComment.dto.PostCommentReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name="PostComment")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @Column(nullable = false)
    private Long writer_id;

    @Column(nullable = false)
    private String writer_name;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    public static PostComment toEntity(PostCommentReqDto reqDto, PostDao postDao, Member member) {
        Post postById = postDao.findPostById(reqDto.getPostId());
        return PostComment.builder()
                .post(postById)
                .writer_id(member.getMember_id())
                .writer_name(member.getSt_nickname())
                .content(reqDto.getContent())
                .build();
    }
}
