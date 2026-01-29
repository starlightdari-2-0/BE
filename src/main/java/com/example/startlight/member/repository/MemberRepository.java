package com.example.startlight.member.repository;

import com.example.startlight.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m.profile_img from Member m where m.member_id =:memberId")
    String getProfileImgUrl(@Param("memberId") Long memberId);
}
