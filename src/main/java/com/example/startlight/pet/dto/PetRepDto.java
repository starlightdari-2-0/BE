package com.example.startlight.pet.dto;

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

    private String pet_name;

    private String species;

    private Gender gender;

    private String birth_date;

    private String death_date;

    private Personality personality;

    private String nickname;

    public static PetRepDto toDto(Pet pet) {
        return PetRepDto.builder()
                .member_id(pet.getMember().getMember_id())
                .pet_id(pet.getPet_id())
                .pet_img(pet.getPet_img())
                .pet_name(pet.getPet_name())
                .species(pet.getSpecies())
                .gender(pet.getGender())
                .birth_date(pet.getBirth_date())
                .death_date(pet.getDeath_date())
                .personality(pet.getPersonality())
                .nickname(pet.getNickname())
                .build();
    }
}
