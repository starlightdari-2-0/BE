package com.example.startlight.pet.dto;

import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PetMyPageRepDto {

    private Long pet_id;

    private String pet_img;

    private String pet_name;

    private Integer star_count;
}
