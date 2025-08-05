package com.example.startlight.memComment.dao;

import com.example.startlight.exception.UnauthorizedAccessException;
import com.example.startlight.memComment.entity.MemComment;
import com.example.startlight.memComment.repository.MemCommentRepository;
import com.example.startlight.memoryStar.entity.MemoryStar;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemCommentDao {
    private final MemCommentRepository memCommentRepository;

    @Transactional
    public MemComment create(MemComment memComment) {
        MemComment comment = memCommentRepository.save(memComment);
        memComment.getMemoryStar().createComment();
        return comment;
    }

    @Transactional
    public MemComment update(Long comment_id, Long user_id, String content) {
        MemComment memComment = memCommentRepository.findById(comment_id)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + comment_id));

        if (!memComment.getWriter_id().equals(user_id)) {
            throw new UnauthorizedAccessException("자신이 작성한 댓글만 수정할 수 있습니다.");
        }

        memComment.updateContent(content);
        return memComment;
    }

    @Transactional
    public void delete(Long user_id, Long comment_id) {
        MemComment memComment = memCommentRepository.findById(comment_id)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + comment_id));

        // ✅ 현재 사용자가 댓글 작성자인지 확인
        if (!memComment.getWriter_id().equals(user_id)) {
            throw new UnauthorizedAccessException("자신이 작성한 댓글만 삭제할 수 있습니다.");
        }

        memCommentRepository.deleteById(comment_id);
        memComment.getMemoryStar().deleteComment();
    }

    public List<MemComment> findAllByMemoryId(Long memory_id) {
        return memCommentRepository.findAllByMemoryIdDesc(memory_id);
    }
}
