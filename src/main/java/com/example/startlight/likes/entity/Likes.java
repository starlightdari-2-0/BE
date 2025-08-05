package com.example.startlight.likes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "likes")
public class Likes {
    @Id
    @GeneratedValue
    private Long likes_id;

    @NotNull
    private String target_type;

    @NotNull
    private Long target_id;

    @NotNull
    private Long member_id;
}
