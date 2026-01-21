package com.example.startlight.member.entity;

import com.example.startlight.member.dto.MemberDto;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.community.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "Member")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private Long member_id;

    @Column(nullable = false)
    private String kk_nickname;

    @Column(nullable = false)
    private String profile_img;

    private String email;

    private String st_nickname;

    @Builder.Default
    @ColumnDefault("0")
    private Integer memory_num = 0;

    @OneToMany
    private List<Post> posts;

    @OneToMany
    private List<Pet> pets;

    public static Member toEntity(MemberDto dto) {
        return Member.builder()
                .kk_nickname(dto.getKk_nickname())
                .profile_img(dto.getProfile_img())
                .email(dto.getEmail())
                .st_nickname(dto.getSt_nickname()).build();
    }

    public void updateMemoryNum() {
        this.memory_num += 1;
    }
}
