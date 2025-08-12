package com.example.startlight.memComReply.dao;

import com.example.startlight.likes.dao.LikesDao;
import com.example.startlight.memComReply.entity.MemComReply;
import com.example.startlight.memComReply.repository.MemComReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemComReplyDao{
    private final MemComReplyRepository memComReplyRepository;
    private final LikesDao likesDao;

    public MemComReply create(MemComReply memComReply){ return memComReplyRepository.save(memComReply);}

    public MemComReply findById(Long id){ return memComReplyRepository.findById(id).orElse(null);}

    public List<MemComReply> findByCommentId(Long commentId){
        return memComReplyRepository.findAllByMemComment_Comment_id(commentId);
    }

    @Transactional
    public MemComReply update(Long replyId, String content) {
        Optional<MemComReply> replyOptional = memComReplyRepository.findById(replyId);
        if(replyOptional.isPresent()){
            MemComReply memComReply = replyOptional.get();
            memComReply.updateReply(content);
            return memComReply;
        }
        else {
            throw new NoSuchElementException("Reply not found");
        }
    }

    public void delete(Long replyId) {
        memComReplyRepository.deleteById(replyId);
    }

    public MemComReply pressLike(Long replyId, Long userId) {
        MemComReply memComReply = findById(replyId);
        likesDao.createReplyLike(memComReply.getMemComment().getComment_id(), userId);
        memComReply.createLikes();
        return memComReply;
    }

    public MemComReply deleteLike(Long replyId, Long userId) {
        MemComReply memComReply = findById(replyId);
        likesDao.deleteReplyLike(userId, memComReply.getMemComment().getComment_id());
        memComReply.deleteLikes();
        return memComReply;
    }

}
