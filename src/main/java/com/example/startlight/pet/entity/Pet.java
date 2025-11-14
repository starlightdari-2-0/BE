package com.example.startlight.pet.entity;

import com.example.startlight.constellation.entity.AnimalCategory;
import com.example.startlight.constellation.entity.AnimalType;
import com.example.startlight.constellation.repository.AnimalTypeRepository;
import com.example.startlight.member.entity.Member;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.pet.dto.PetReqDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "Pet")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pet_id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    private String pet_img;

    @ManyToOne
    @JoinColumn(name = "animal_type_id")
    private AnimalType animal_type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimalCategory animal_category;

    private String species;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String pet_name;

    @Column(nullable = false)
    private String birth_date;

    @Column(nullable = false)
    private String first_date;

    private String death_date;

    private Personality personality;

    private String nickname; // 호칭

    private String context; // 한줄 기록

    private Long con_id; // 선택한 별자리 id

    public static Pet toEntity(PetReqDto dto, Member member, AnimalType animalType) {

        // Pet 엔티티 생성
        return Pet.builder()
                .member(member)
                .pet_name(dto.getPet_name())
                .animal_type(animalType)
                .animal_category(animalType.getCategory())
                .species(dto.getSpecies())
                .gender(dto.getGender())
                .birth_date(dto.getBirth_date())
                .first_date(dto.getFirst_date())
                .death_date(dto.getDeath_date())
                .personality(dto.getPersonality())
                .nickname(dto.getNickname())
                .context(dto.getContext())
                .con_id(dto.getCon_id())
                .build();
    }
}
