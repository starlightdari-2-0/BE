package com.example.startlight.member.service;

import com.example.startlight.kakao.dto.KakaoUserCreateDto;
import com.example.startlight.member.dto.MemberDto;
import com.example.startlight.member.dto.MemberWithPetDto;

public interface MemberService {
    MemberDto createMember(MemberDto memberDto);

    MemberDto selectCurrentMember();

    MemberDto updateMemberName(String nickname);

    void deleteMember(Long id);

    MemberDto loginMember(KakaoUserCreateDto kakaoUserCreateDto);

    Long getLoginedUserId();

    void updateMemberMemory();

    Integer getMemoryNumber();

    MemberWithPetDto getMemberWithPet();
}
