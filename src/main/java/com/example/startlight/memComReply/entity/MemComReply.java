package com.example.startlight.memComReply.entity;

import com.example.startlight.memComReply.dto.MemComReplyRepDto;
import com.example.startlight.memComment.entity.MemComment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MemComReply")
public class MemComReply {
    @Id
    @GeneratedValue
    private Long reply_id;

    private String content;

    private Long writer_id;

    private String writer_name;

    @Builder.Default
    @ColumnDefault("0")
    private Integer likes = 0;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private MemComment memComment;

    public void updateReply(String content) {
        this.content = content;
    }

    public void createLikes() { this.likes++; }
    public void deleteLikes() { this.likes--; }

    public MemComReplyRepDto toResponseDto() {
        return MemComReplyRepDto.builder()
                .reply_id(reply_id)
                .content(content)
                .writer_id(writer_id)
                .writer_name(writer_name)
                .likes(likes)
                .comment_id(memComment.getComment_id())
                .build();
    }
}
