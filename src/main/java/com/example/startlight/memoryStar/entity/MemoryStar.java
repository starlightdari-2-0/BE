package com.example.startlight.memoryStar.entity;

import com.example.startlight.memComment.entity.MemComment;
import com.example.startlight.memoryStar.dto.MemoryStarUpdateDto;
import com.example.startlight.starReaction.entity.ReactionType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name="MemoryStar")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemoryStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memory_id;

    @Column(nullable = false)
    private Long star_node_id;

    @Column(nullable = false)
    private Long writer_id;

    @Column(nullable = false)
    private String writer_name;

    @Column(nullable = false)
    private Long pet_id;

    @Column(nullable = false)
    private String name;

    private ActivityCtg activityCtg;

    private String content;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean shared = false;

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

    @OneToMany
    private List<MemComment> memComments;
    
    public void updateMemoryStar(MemoryStarUpdateDto dto) {
        this.name = dto.getName();
        this.activityCtg = dto.getActivityCtg();
        this.content = dto.getContent();
        this.shared = dto.getShared();
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

}
