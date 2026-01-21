package com.example.startlight.member.service;

import com.example.startlight.infra.kakao.dto.KakaoUserCreateDto;
import com.example.startlight.member.dto.MemberDto;
import com.example.startlight.member.dto.MemberWithPetDto;

public interface MemberService {
    MemberDto createMember(MemberDto memberDto);

    MemberDto selectCurrentMember();

    String updateMemberName(String nickname);

    void deleteMember(Long id);

    MemberDto loginMember(KakaoUserCreateDto kakaoUserCreateDto);

    Long getLoginedUserId();

    void updateMemberMemory();

    Integer getMemoryNumber();

    MemberWithPetDto getMemberWithPet();

    boolean isFirstKakaoLogin(Long id);
}
