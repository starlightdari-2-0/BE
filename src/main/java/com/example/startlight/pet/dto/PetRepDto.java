package com.example.startlight.pet.dto;

import com.example.startlight.constellation.entity.AnimalCategory;
import com.example.startlight.pet.entity.Gender;
import com.example.startlight.pet.entity.Personality;
import com.example.startlight.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PetRepDto {

    private Long member_id;

    private Long pet_id;

    private String pet_img;

    private AnimalCategory animal_category;

    private String species;

    private Gender gender;

    private String pet_name;

    private String birth_date;

    private String first_date;

    private String death_date;

    private Personality personality;

    private String nickname;

    private String context;

    public static PetRepDto toDto(Pet pet) {
        return PetRepDto.builder()
                .member_id(pet.getMember().getMember_id())
                .pet_id(pet.getPet_id())
                .pet_img(pet.getPet_img())
                .pet_name(pet.getPet_name())
                .animal_category(pet.getAnimal_category())
                .species(pet.getSpecies())
                .gender(pet.getGender())
                .birth_date(pet.getBirth_date())
                .first_date(pet.getFirst_date())
                .death_date(pet.getDeath_date())
                .personality(pet.getPersonality())
                .nickname(pet.getNickname())
                .context(pet.getContext())
                .build();
    }
}
