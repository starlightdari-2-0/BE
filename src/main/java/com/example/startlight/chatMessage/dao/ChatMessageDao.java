package com.example.startlight.chatMessage.dao;

import com.example.startlight.chatMessage.entity.ChatMessage;
import com.example.startlight.chatMessage.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageDao {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findMessageByUserId(Long userId) {
        return chatMessageRepository.findAllByMemberId(userId);
    }
}
