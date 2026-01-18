package com.example.startlight.memComment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private boolean last;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private int size;
    private int number;
    private int numberOfElements;
    private boolean empty;
}
