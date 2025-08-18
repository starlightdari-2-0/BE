package com.example.startlight.pet.entity;

import com.example.startlight.member.entity.Member;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.pet.dto.PetReqDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private String species;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String pet_name;

    private String birth_date;

    private String death_date;

    private Personality personality;

    private String nickname;

    private String svg_path;

    @ElementCollection
    @CollectionTable(name = "starlist_edges", joinColumns = @JoinColumn(name = "pet_id"))
    private List<Edge> edges = new ArrayList<>();

    public static Pet toEntity(PetReqDto dto, Long userId, MemberRepository memberRepository) {
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isEmpty()) {
            throw new IllegalArgumentException("Member not found for email: " + userId);
        }

        // Pet 엔티티 생성
        return Pet.builder()
                .member(member.get())
                .pet_name(dto.getPet_name())
                .species(dto.getSpecies())
                .gender(dto.getGender())
                .birth_date(dto.getBirth_date())
                .death_date(dto.getDeath_date())
                .personality(dto.getPersonality())
                .build();
    }
}
