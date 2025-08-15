package com.example.startlight.memComReply.entity;

import com.example.startlight.memComReply.dto.MemComReplyRepDto;
import com.example.startlight.memComment.entity.MemComment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MemComReply")
@EntityListeners(AuditingEntityListener.class)
public class MemComReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    private String content;

    private Long writerId;

    private String writer_name;

    @Builder.Default
    @ColumnDefault("0")
    private Integer likes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private MemComment memComment;

    public void updateReply(String content) {
        this.content = content;
    }

    public void createLikes() { this.likes++; }
    public void deleteLikes() { this.likes--; }

    public MemComReplyRepDto toResponseDto() {
        return MemComReplyRepDto.builder()
                .reply_id(replyId)
                .content(content)
                .writer_id(writerId)
                .writer_name(writer_name)
                .likes(likes)
                .comment_id(memComment.getComment_id())
                .build();
    }
}
