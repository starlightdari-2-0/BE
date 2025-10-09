package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "animal_type")
public class AnimalType {

    @Id
    private Long animal_type_id;

    @Enumerated(EnumType.STRING)
    private AnimalCategory category;

    private String name;
}
