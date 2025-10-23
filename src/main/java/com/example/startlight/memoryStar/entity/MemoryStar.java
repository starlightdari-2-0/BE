package com.example.startlight.memoryStar.entity;

import com.example.startlight.memComment.entity.MemComment;
import com.example.startlight.memLike.entity.MemLike;
import com.example.startlight.memoryStar.dto.MemoryStarUpdateDto;
import com.example.startlight.starList.entity.StarList;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
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
    private Boolean shared = false;  // 기본값 설정

    @Builder.Default
    private Boolean updated = false;

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
    private Integer commentNumber = 0;  // 기본값 설정

    @Setter
    private String img_url;

    @OneToMany
    private List<MemComment> memComments;

    @OneToMany
    private List<MemLike> memLikes;
    
    public void updateMemoryStar(MemoryStarUpdateDto dto) {
        this.name = dto.getName();
        this.activityCtg = dto.getActivityCtg();
        this.content = dto.getContent();
        this.shared = dto.getShared();
        this.updated = true;
    }

    public void createLike(int type) {
        switch (type) {
            case 1:
                this.like1++;
                break;
            case 2:
                this.like2++;
                break;
            case 3:
                this.like3++;
                break;
        }
    }

    public void deleteLike(int type) {
        switch (type) {
            case 1:
                this.like1--;
                break;
            case 2:
                this.like2--;
                break;
            case 3:
                this.like3--;
                break;
        }
    }

    public void createComment() {
        this.commentNumber++;
    }

    public void deleteComment() {
        this.commentNumber--;
    }

}
