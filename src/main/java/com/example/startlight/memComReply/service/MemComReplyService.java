package com.example.startlight.memComReply.service;

import com.example.startlight.memComReply.dao.MemComReplyDao;
import com.example.startlight.memComReply.dto.MemComReplyRepDto;
import com.example.startlight.memComReply.dto.MemComReplyReqDto;
import com.example.startlight.memComReply.entity.MemComReply;
import com.example.startlight.memComment.dao.MemCommentDao;
import com.example.startlight.memComment.entity.MemComment;
import com.example.startlight.memComment.repository.MemCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemComReplyService {
    private final MemComReplyDao memComReplyDao;
    private final MemCommentDao memCommentDao;

    public MemComReplyRepDto createReply(MemComReplyReqDto memComReplyReqDto) {
        MemComment memComment = memCommentDao.findById(memComReplyReqDto.getCommentId());
        MemComReply memComReply = MemComReply.builder()
                .content(memComReplyReqDto.getContent())
                .writer_id(1L)
                .writer_name("test")
                .memComment(memComment).build();
        MemComReply createdReply = memComReplyDao.create(memComReply);
        return createdReply.toResponseDto();
    }

    public MemComReplyRepDto updateReply(Long id, String content) {
        MemComReply memComReply = memComReplyDao.update(id, content);
        return memComReply.toResponseDto();
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
        memComReplyDao.delete(id);
    }

    public MemComReply pressLike(Long id) {
        return memComReplyDao.pressLike(id, 1L);
    }

    public MemComReply deleteLike(Long id) {
        return memComReplyDao.deleteLike(id, 1L);
    }
}
