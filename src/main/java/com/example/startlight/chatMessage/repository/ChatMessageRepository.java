package com.example.startlight.chatMessage.repository;

import com.example.startlight.chatMessage.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select c from ChatMessage c where c.member.member_id = :memberId")
    List<ChatMessage> findAllByMemberId(@Param("memberId") Long memberId);
}
