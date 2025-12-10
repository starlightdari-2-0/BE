package com.example.startlight.likes.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.likes.dao.LikesDao;
import com.example.startlight.likes.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesDao likesDao;
    private final LikesRepository likesRepository;

    public boolean createLike(Long commentId) {
        Long userId = UserUtil.getCurrentUserId();
        likesDao.createReplyLike(userId, commentId);
        return true;
    }

    public boolean deleteLike(Long commentId) {
        Long userId = UserUtil.getCurrentUserId();
        likesDao.deleteReplyLike(userId, commentId);
        return true;
    }

    public Long getLikeCount(Long commentId) {
        return likesRepository.findLikesCountByTarget_id(commentId);
    }
}
