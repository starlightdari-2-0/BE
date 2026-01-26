package com.example.startlight.member.controller;

import com.example.startlight.member.dto.ActivityPostDto;
import com.example.startlight.member.service.MemberActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class MemberActivityController {
    private final MemberActivityService memberActivityService;

    @GetMapping("/post")
    public ResponseEntity<List<ActivityPostDto>> getActivityPost() {
        List<ActivityPostDto> postActivities = memberActivityService.getPostActivities();
        return ResponseEntity.ok(postActivities);
    }

}
