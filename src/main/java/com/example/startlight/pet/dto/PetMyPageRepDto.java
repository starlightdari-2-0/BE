package com.example.startlight.pet.dto;

import com.example.startlight.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PetMyPageRepDto {

    private Long pet_id;

    private String pet_svg;

    private String pet_name;

    public static PetMyPageRepDto toPetMyPageRepDto(Pet pet) {
        return PetMyPageRepDto.builder()
                .pet_id(pet.getPet_id())
                .pet_svg(pet.getSvg_path())
                .pet_name(pet.getPet_name()).build();
    }
}
