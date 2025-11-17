package com.example.startlight.starReaction.entity;

import com.example.startlight.member.entity.Member;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.reactionType.entity.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "star_reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_star_reaction_star_user_type",
                        columnNames = {"star_id", "member_id", "type_id"}
                )
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    private MemoryStar memoryStar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ReactionType reactionType;
}
