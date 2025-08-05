package com.example.startlight.member.dto;

import com.example.startlight.pet.dto.PetSimpleRepDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemberWithPetDto {
    private String profileUrl;
    private String name;
    private List<PetSimpleRepDto> petList;
}
