package com.example.startlight.memory.memComment.dao;

import com.example.startlight.global.exception.UnauthorizedAccessException;
import com.example.startlight.memory.memComment.entity.MemComment;
import com.example.startlight.memory.memComment.repository.MemCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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

        if (!memComment.getWriter_id().equals(user_id)) {
            throw new UnauthorizedAccessException("자신이 작성한 댓글만 삭제할 수 있습니다.");
        }

        memCommentRepository.deleteById(comment_id);
        memComment.getMemoryStar().deleteComment();
    }

    public Page<MemComment> findParentCommentByMemoryId(Long memoryId, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return memCommentRepository.findParentCommentByMemoryId(memoryId, pageable);
    }

    public List<MemComment> findAllByMemoryId(Long memory_id) {
        return memCommentRepository.findAllByMemoryIdDesc(memory_id);
    }

    public MemComment findById(Long comment_id) {
        Optional<MemComment> byId = memCommentRepository.findById(comment_id);
        if (byId.isPresent()) {
            return byId.get();
        }
        else throw new EntityNotFoundException();
    }

    public List<MemComment> findChildrenCommentByCommentId(Long comment_id) {
        return memCommentRepository.findChildrenCommentByCommentId(comment_id);
    }

    public Long countChildrenComment(Long comment_id) {
        return memCommentRepository.countChildrenComment(comment_id);
    }
}
