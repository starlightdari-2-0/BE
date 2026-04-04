package com.example.startlight.community.post.postReaction.entity;

import com.example.startlight.community.post.entity.Post;
import com.example.startlight.global.entity.ReactionType;
import com.example.startlight.member.entity.Member;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_reaction_post_user_type",
                        columnNames = {"post_id", "member_id", "reaction_type"}
                )
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private ReactionType reactionType;
}
