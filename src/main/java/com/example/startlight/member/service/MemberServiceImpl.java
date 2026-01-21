package com.example.startlight.member.service;

import com.example.startlight.infra.kakao.dto.KakaoUserCreateDto;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.dto.MemberDto;
import com.example.startlight.member.dto.MemberWithPetDto;
import com.example.startlight.member.entity.Member;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.pet.dto.PetSimpleRepDto;
import com.example.startlight.pet.service.PetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberDao memberDao;
    private final MemberRepository memberRepository;
    private final PetService petService;

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        Member member = memberDao.createMember(Member.toEntity(memberDto));
        return MemberDto.toDto(member);
    }

    @Override
    public MemberDto selectCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);

        if (authentication != null && authentication.getPrincipal() instanceof Map) {
            Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
            Long id =  (Long) principal.get("id");
            Member member = memberDao.selectMember(id);
            return MemberDto.toDto(member);
        }

        throw new IllegalStateException("User is not authenticated");
    }

    @Override
    public String updateMemberName(String nickname) {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.updateMemberName(userId,nickname);
        return member.getSt_nickname();
    }

    @Override
    public void deleteMember(Long id) {
        memberDao.deleteMember(id);
    }

    @Override
    public MemberDto loginMember(KakaoUserCreateDto kakaoUserCreateDto) {
        Optional<Member> memberOptional = memberRepository.findById(kakaoUserCreateDto.getId());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return MemberDto.toDto(member);
        }
        else {
            Member member = Member.builder().member_id(kakaoUserCreateDto.getId())
                    .kk_nickname(kakaoUserCreateDto.getNickName())
                    // 최초 로그인 시 시스템 닉네임을 카카오 닉네임으로 설정
                    .st_nickname(kakaoUserCreateDto.getNickName())
                    .email(kakaoUserCreateDto.getEmail())
                    .profile_img(kakaoUserCreateDto.getProfileImageUrl()).build();
            memberDao.createMember(member);
            return MemberDto.toDto(member);
        }
    }

    @Override
    public Long getLoginedUserId() {
        return UserUtil.getCurrentUserId();
    }


    @Override
    public void updateMemberMemory() {
        Long userId = UserUtil.getCurrentUserId();
        memberDao.updateMemberMemory(userId);
    }

    @Override
    public Integer getMemoryNumber() {
        Long userId = UserUtil.getCurrentUserId();
        return memberDao.getMemoryNum(userId);
    }

    @Override
    public MemberWithPetDto getMemberWithPet() {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        String profileUrl = member.getProfile_img();
        String kkNickname = member.getKk_nickname();
        List<PetSimpleRepDto> petSimple = petService.getPetSimple(userId);
        return MemberWithPetDto.builder()
                .profileUrl(profileUrl)
                .name(kkNickname)
                .petList(petSimple)
                .build();
    }

    @Override
    public boolean isFirstKakaoLogin(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.isPresent();
    }
}
