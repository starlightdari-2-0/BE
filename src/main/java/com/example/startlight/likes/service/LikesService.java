package com.example.startlight.likes.service;

import com.example.startlight.likes.dao.LikesDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesDao likesDao;
}
