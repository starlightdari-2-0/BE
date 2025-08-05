package com.example.startlight.pet.dto;

import com.example.startlight.pet.entity.Gender;
import com.example.startlight.pet.entity.Personality;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class PetUpdateReqDto {

    private String pet_name;

    private String species;

    private Gender gender;

    private String birth_date;

    private String death_date;

    private Personality personality;

    private String nickname;
}
