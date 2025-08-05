package com.example.startlight.pet.dto;

import com.example.startlight.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PetSimpleRepDto {
    private Long pet_id;

    private String pet_img;

    private String pet_name;

    public static PetSimpleRepDto toDto(Pet pet) {
        return PetSimpleRepDto.builder()
                .pet_id(pet.getPet_id())
                .pet_img(pet.getPet_img())
                .pet_name(pet.getPet_name())
                .build();
    }
}
