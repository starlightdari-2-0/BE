package com.example.startlight.likes.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.likes.dao.LikesDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesDao likesDao;

    public boolean createLike(Long commentId) {
        Long userId = UserUtil.getCurrentUserId();
        likesDao.createReplyLike(userId, commentId);
        return true;
    }
}
