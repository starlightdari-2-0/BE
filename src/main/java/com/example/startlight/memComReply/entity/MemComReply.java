package com.example.startlight.memComReply.entity;

import com.example.startlight.memComment.entity.MemComment;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "MemComReply")
public class MemComReply {
    @Id
    @GeneratedValue
    private Long reply_id;

    private String content;

    private Long writer_id;

    private String writer_name;

    private Long likes;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private MemComment memComment;
}
