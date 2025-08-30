package com.example.startlight.memComReply.service;

import com.example.startlight.exception.UnauthorizedAccessException;
import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.memComReply.dao.MemComReplyDao;
import com.example.startlight.memComReply.dto.MemComReplyRepDto;
import com.example.startlight.memComReply.dto.MemComReplyReqDto;
import com.example.startlight.memComReply.entity.MemComReply;
import com.example.startlight.memComment.dao.MemCommentDao;
import com.example.startlight.memComment.entity.MemComment;
import com.example.startlight.memComment.repository.MemCommentRepository;
import com.example.startlight.member.dao.MemberDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemComReplyService {
    private final MemComReplyDao memComReplyDao;
    private final MemCommentDao memCommentDao;
    private final MemberDao memberDao;

    public MemComReplyRepDto createReply(MemComReplyReqDto memComReplyReqDto) {
        Long userId = UserUtil.getCurrentUserId();
        String writerName = memberDao.getMemberName(userId);
        MemComment memComment = memCommentDao.findById(memComReplyReqDto.getCommentId());
        MemComReply memComReply = MemComReply.builder()
                .content(memComReplyReqDto.getContent())
                .writerId(userId)
                .writer_name(writerName)
                .memComment(memComment).build();
        MemComReply createdReply = memComReplyDao.create(memComReply);
        return createdReply.toResponseDto();
    }

    public MemComReplyRepDto updateReply(Long id, String content) {
        Long userId = UserUtil.getCurrentUserId();
        MemComReply memComReply = memComReplyDao.findById(id);

        if (!userId.equals(memComReply.getWriterId())) {
            throw new UnauthorizedAccessException("댓글 수정 권한이 없습니다.");
        }

        MemComReply updatedReply = memComReplyDao.update(id, content);
        return updatedReply.toResponseDto();
    }

    public List<MemComReplyRepDto> findAllReplies(Long commentId) {
        List<MemComReply> byCommentId = memComReplyDao.findByCommentId(commentId);
        List<MemComReplyRepDto> replyRepDtos = new ArrayList<>();
        for (MemComReply memComReply : byCommentId) {
            replyRepDtos.add(memComReply.toResponseDto());
        }
        return replyRepDtos;
    }

    public void deleteReply(Long id) {
        Long userId = UserUtil.getCurrentUserId();
        MemComReply memComReply = memComReplyDao.findById(id);
        if (!userId.equals(memComReply.getWriterId())) {
            throw new UnauthorizedAccessException("댓글 삭제 권한이 없습니다.");
        }
        memComReplyDao.delete(id);
    }

    public MemComReply pressLike(Long id) {
        Long userId = UserUtil.getCurrentUserId();
        return memComReplyDao.pressLike(id, userId);
    }

    public MemComReply deleteLike(Long id) {
        Long userId = UserUtil.getCurrentUserId();
        return memComReplyDao.deleteLike(id, userId);
    }
}
