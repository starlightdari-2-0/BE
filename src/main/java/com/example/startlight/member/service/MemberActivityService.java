package com.example.startlight.member.service;

import com.example.startlight.community.post.entity.Post;
import com.example.startlight.community.post.repository.PostRepository;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dto.ActivityCommentDto;
import com.example.startlight.member.dto.ActivityPostDto;
import com.example.startlight.memory.memComment.entity.MemComment;
import com.example.startlight.memory.memComment.repository.MemCommentRepository;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@RequiredArgsConstructor
@Service
public class MemberActivityService {

    private final MemoryStarRepository memoryStarRepository;
    private final PostRepository postRepository;
    private final PetRepository petRepository;
    private final MemCommentRepository memCommentRepository;

    //나의 활동 - 게시글
    public List<ActivityPostDto> getPostActivities() {
        Long userId = UserUtil.getCurrentUserId();
        List<MemoryStar> memoryStarList = memoryStarRepository.findAllByWriterId(userId);
        List<Post> postList = postRepository.findAllByMemberId(userId);
        List<ActivityPostDto> activityPostDtos = new ArrayList<>();
        for (MemoryStar memoryStar : memoryStarList) {
            String petName = petRepository.findPetNameById(memoryStar.getPet_id());
            ActivityPostDto dto = new ActivityPostDto(
                    memoryStar.getName(),
                    petName,
                    memoryStar.getUpdatedAt(),
                    memoryStar.getLikeCounts(),
                    memoryStar.getCommentNumber()
            );
            activityPostDtos.add(dto);
        }
        for (Post p : postList) {
            // TODO : add likeCount, commentCount logic
            ActivityPostDto dto = new ActivityPostDto(
                    p.getTitle(),
                    p.getMember().getSt_nickname(),
                    p.getUpdatedAt(),
                    0,0
            );
            activityPostDtos.add(dto);
        }
        // 최신순 정렬 (createdAt/updatedAt 기준)
        activityPostDtos.sort(
                Comparator.comparing(
                        ActivityPostDto::updatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );
        return activityPostDtos;
    }

    //나의 활동 - 댓글
    public List<ActivityCommentDto> getCommentActivities() {
        Long userId = UserUtil.getCurrentUserId();
        List<MemComment> memCommentList = memCommentRepository.findAllByMemberId(userId);
        List<ActivityCommentDto> activityCommentDtos = new ArrayList<>();
        for (MemComment comment : memCommentList) {
            Integer replyCount = memCommentRepository.countReplies(comment.getComment_id());
            ActivityCommentDto dto = new ActivityCommentDto(
                    comment.getContent(),
                    comment.getWriter_name(),
                    comment.getCreatedAt(),
                    replyCount
            );
            activityCommentDtos.add(dto);
        }
        // TODO : 커뮤니티 댓글 조회 로직 추가
        activityCommentDtos.sort(
                Comparator.comparing(
                        ActivityCommentDto::updatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );
        return activityCommentDtos;
    }
}
