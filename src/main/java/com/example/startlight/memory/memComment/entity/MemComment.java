package com.example.startlight.memory.memComment.entity;

import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="MemComment")
public class MemComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    private MemoryStar memoryStar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MemComment parent;

    @OneToMany(mappedBy = "parent")
    private List<MemComment> children = new ArrayList<>();

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long writer_id;

    @Column(nullable = false)
    private String writer_name;

    private LocalDateTime createdAt;

    public void updateContent(String content) {
        this.content = content;
    }
}
