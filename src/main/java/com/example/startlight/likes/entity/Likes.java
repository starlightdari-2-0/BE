package com.example.startlight.likes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likes_id;

    @Column(nullable = false)
    private Long member_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType target_type;

    @Column(nullable = false)
    private Long target_id;
}
