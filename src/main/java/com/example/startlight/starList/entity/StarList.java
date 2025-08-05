package com.example.startlight.starList.entity;

import com.example.startlight.memoryStar.dto.MemoryStarUpdateDto;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="StarList")
public class StarList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long star_id;

    private Integer index_id;

    @ManyToOne
    private Pet pet;

    @Column(nullable = false)
    private Integer x_star;

    @Column(nullable = false)
    private Integer y_star;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean written = false;

    @OneToOne
    @JoinColumn(name = "memory_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private MemoryStar memoryStar;

    public void updateStarWritten(MemoryStar createdStar) {
        this.memoryStar = createdStar;
        this.written = Boolean.TRUE;
    }

    public void updateStarUnWritten() {this.written = Boolean.FALSE;}
}
