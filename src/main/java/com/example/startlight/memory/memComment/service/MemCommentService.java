package com.example.startlight.memory.memComment.service;

import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.likes.service.LikesService;
import com.example.startlight.memory.memComment.dao.MemCommentDao;
import com.example.startlight.memory.memComment.dto.MemCommentRepDto;
import com.example.startlight.memory.memComment.dto.MemCommentReqDto;
import com.example.startlight.memory.memComment.dto.MemCommentUpdateReqDto;
import com.example.startlight.memory.memComment.dto.PageResponse;
import com.example.startlight.memory.memComment.entity.MemComment;
import com.example.startlight.memory.memComment.mapper.MemCommentMapper;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.memory.memoryStar.dao.MemoryStarDao;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemCommentService {
    private final MemCommentDao memCommentDao;
    private final MemCommentMapper mapper = MemCommentMapper.INSTANCE;
    private final MemoryStarDao memoryStarDao;
    private final MemberDao memberDao;
    private final LikesService likesService;

    public MemCommentRepDto saveMemComment(MemCommentReqDto memCommentReqDto) {
        Long userId = UserUtil.getCurrentUserId();
        String stNickname = memberDao.selectMember(userId).getSt_nickname();
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(memCommentReqDto.getMemory_id());
        MemComment parentComment = null;
        if (memCommentReqDto.getParent_id() != null) {
            // 대댓글
            parentComment = memCommentDao.findById(memCommentReqDto.getParent_id());
            if (!parentComment.getMemoryStar().getMemory_id().equals(memoryStar.getMemory_id())) {
                // 부모 댓글과 자식 댓글의 MemoryId가 같지 않으면
                throw new IllegalArgumentException("부모 댓글이 다른 MemoryStar에 속해 있습니다.");
            }
        }
        MemComment memComment = MemComment.builder()
                .content(memCommentReqDto.getContent())
                .writer_id(userId)
                .writer_name(stNickname)
                .memoryStar(memoryStar)
                .parent(parentComment)
                .build();
        MemComment memComment1 = memCommentDao.create(memComment);
        return mapper.toDto(memComment1);
    }

    public MemCommentRepDto updateMemComment(MemCommentUpdateReqDto dto) {
        Long userId = UserUtil.getCurrentUserId();
        MemComment memComment = memCommentDao.update(dto.getComment_id(), userId, dto.getContent());
        return mapper.toDto(memComment);
    }

    public void deleteMemComment(Long comment_id) {
        Long userId = UserUtil.getCurrentUserId();
        memCommentDao.delete(userId, comment_id);
    }

    public PageResponse<MemCommentRepDto> findParentCommentByMemoryId(Long memory_id, int page) {
        Page<MemComment> commentPage = memCommentDao.findParentCommentByMemoryId(memory_id, page);
        Page<MemCommentRepDto> dtoPage = commentPage.map(mapper::toDto);
        List<MemCommentRepDto> contentWithFlag = dtoPage.getContent().stream()
                .peek(dto -> {
                    boolean mine = checkIfMine(dto.getComment_id());
                    dto.setMine(mine);
                    Long replyCount = memCommentDao.countChildrenComment(dto.getComment_id());
                    dto.setReply_count(replyCount);
                    Long likeCount = likesService.getLikeCount(dto.getComment_id());
                    dto.setLike_count(likeCount);
                    boolean isLiked = likesService.findIfILiked(dto.getComment_id());
                    dto.setLike(isLiked);
                })
                .toList();

        return new PageResponse<>(
                contentWithFlag,
                dtoPage.isLast(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.getSize(),
                dtoPage.getNumber(),
                dtoPage.getNumberOfElements(),
                dtoPage.isEmpty()
        );
    }

    public List<MemCommentRepDto> findAllByMemoryId(Long memory_id) {
        List<MemComment> allByMemoryId = memCommentDao.findAllByMemoryId(memory_id);
        return allByMemoryId.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public boolean checkIfMine(Long comment_id) {
        Long userId = UserUtil.getCurrentUserId();
        MemComment memComment = memCommentDao.findById(comment_id);
        return userId.equals(memComment.getWriter_id());
    }

    public List<MemCommentRepDto> findChildrenCommentByCommentId(Long comment_id) {
        List<MemComment> comments = memCommentDao.findChildrenCommentByCommentId(comment_id);
        List<MemCommentRepDto> commentRepDtos = comments.stream().map(mapper::toDto).toList();
        for (MemCommentRepDto commentRepDto : commentRepDtos) {
            commentRepDto.setMine(checkIfMine(commentRepDto.getComment_id()));
            Long likeCount = likesService.getLikeCount(commentRepDto.getComment_id());
            commentRepDto.setLike_count(likeCount);
            boolean ifILiked = likesService.findIfILiked(commentRepDto.getComment_id());
            commentRepDto.setLike(ifILiked);
        }
        return commentRepDtos;
    }
}
