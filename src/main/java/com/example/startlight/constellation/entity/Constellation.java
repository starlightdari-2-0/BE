package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "constellation")
public class Constellation {
    @Id
    private Long con_id;

    @Column(nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "animal_type_id")
    private AnimalType animalType;

    private String thumbnail_img;
}
