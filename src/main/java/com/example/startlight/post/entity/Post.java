package com.example.startlight.post.entity;

import com.example.startlight.member.entity.Member;
import com.example.startlight.post.dto.PostRequestDto;
import com.example.startlight.post.dto.PostUpdateReqDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Category category;

    private Long funeral_id;

    @Column(nullable = false)
    private Long report;

    @Setter
    private String img_url;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean updated = false;

    public static Post toEntity(PostRequestDto postRequestDto, Member member) {
        if(postRequestDto.getFuneral_id() != null) {
            return Post.builder()
                    .member(member)
                    .title(postRequestDto.getTitle())
                    .content(postRequestDto.getContent())
                    .category(postRequestDto.getCategory())
                    .funeral_id(postRequestDto.getFuneral_id())
                    .report(0L).build();
        }
        else {
            return Post.builder()
                    .member(member)
                    .title(postRequestDto.getTitle())
                    .content(postRequestDto.getContent())
                    .category(postRequestDto.getCategory())
                    .report(0L).build();
        }
    }

    public void updatePost(PostUpdateReqDto updateReqDto) {
        this.title = updateReqDto.getTitle();
        this.content = updateReqDto.getContent();
        this.updated = true;
        if(updateReqDto.getFuneral_id() != null) {
            this.funeral_id = updateReqDto.getFuneral_id();
        }
    }
}
