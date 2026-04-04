package com.example.startlight.community.post.entity;

import com.example.startlight.global.entity.ReactionType;
import com.example.startlight.member.entity.Member;
import com.example.startlight.community.post.dto.PostRequestDto;
import com.example.startlight.community.post.dto.PostUpdateReqDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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

    @Builder.Default
    private Integer like1 = 0;

    @Builder.Default
    private Integer like2 = 0;

    @Builder.Default
    private Integer like3 = 0;

    @Column(insertable = false, updatable = false)
    private Integer totalLikes;

    @Builder.Default
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer commentNumber = 0;

    @Setter
    private String img_url;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean updated = false;

    public static Post toEntity(PostRequestDto postRequestDto, Member member) {
        return Post.builder()
                .member(member)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .build();
    }

    public void updatePost(PostUpdateReqDto updateReqDto) {
        this.title = updateReqDto.getTitle();
        this.content = updateReqDto.getContent();
        this.updated = true;
    }

    public void increaseLike(ReactionType type) {
        switch (type) {
            case LIKE1 -> this.like1++;
            case LIKE2 -> this.like2++;
            case LIKE3 -> this.like3++;
        }
    }

    public void decreaseLike(ReactionType type) {
        switch (type) {
            case LIKE1 -> {
                if (this.like1 > 0) this.like1--;
            }
            case LIKE2 -> {
                if (this.like2 > 0) this.like2--;
            }
            case LIKE3 -> {
                if (this.like3 > 0) this.like3--;
            }
        }
    }

    public void createComment() {
        this.commentNumber++;
    }

    public void deleteComment() {
        this.commentNumber--;
    }

    public Integer getLikeCounts() { return this.like1 + this.like2 + this.like3; }
}
