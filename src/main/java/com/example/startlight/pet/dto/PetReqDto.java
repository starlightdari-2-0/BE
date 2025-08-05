package com.example.startlight.pet.dto;

import com.example.startlight.pet.entity.Gender;
import com.example.startlight.pet.entity.Personality;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetReqDto {

    private MultipartFile pet_img;

    private String pet_name;

    private String species;

    private Gender gender;

    private String birth_date;

    private String death_date;

    private Personality personality;

    private String nickname;

    private Double selected_x;

    private Double selected_y;
}
