package com.example.startlight.chatMessage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatAnswerDto {
    private Long chatId;
    private String answer;
}
