package com.example.startlight.member.controller;

import com.example.startlight.member.dto.MemberDto;
import com.example.startlight.member.dto.MemberNickNameRepDto;
import com.example.startlight.member.dto.MemberRequestDto;
import com.example.startlight.member.dto.MemberWithPetDto;
import com.example.startlight.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<MemberDto> createUser(
            @RequestBody MemberDto memberDto
    ) {
        MemberDto responseMemberDto = memberService.createMember(memberDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMemberDto);
    }

    @GetMapping()
    public ResponseEntity<MemberDto> selectUser() {
        MemberDto memberDto = memberService.selectCurrentMember();
        return ResponseEntity.status(HttpStatus.OK).body(memberDto);
    }

    @PutMapping("/name")
    public ResponseEntity<Map<String, String>> updateUserName(
            @RequestBody MemberRequestDto memberRequestDto
            ) {
        String updatedMemberName = memberService.updateMemberName(memberRequestDto.getNickname());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("nickname", updatedMemberName));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(
            @RequestBody Long id
    ) {
        memberService.deleteMember(id);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 삭제되었습니다.");
    }

    // 내비게이션 바 정보
    @GetMapping("/nav")
    public ResponseEntity<MemberWithPetDto> getMemberWithPet() {
        MemberWithPetDto memberWithPet = memberService.getMemberWithPet();
        return ResponseEntity.status(HttpStatus.OK).body(memberWithPet);
    }

    // 현재 로그인된 유저 정보
    @GetMapping("/logined")
    public ResponseEntity<Long> getLoginedUserId() {
        Long loginedUserId = memberService.getLoginedUserId();
        return ResponseEntity.status(HttpStatus.OK).body(loginedUserId);
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> getEmail() {
        String email = memberService.selectCurrentMember().getEmail();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("email", email));
    }
}
