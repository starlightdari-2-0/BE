package com.example.startlight.memLike.entity;

import com.example.startlight.memoryStar.entity.MemoryStar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "MemLike", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"memory_id", "member_id"})
})
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MemLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long member_id;

    @ManyToOne
    @JoinColumn(name = "memory_id")
    private MemoryStar memoryStar;
}
