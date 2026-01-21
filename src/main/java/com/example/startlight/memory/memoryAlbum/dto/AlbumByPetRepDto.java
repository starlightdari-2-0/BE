package com.example.startlight.memory.memoryAlbum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumByPetRepDto {
    private Long petId;
    private String petName;
    private String imgUrl;
    private Boolean albumStarted; //추억앨범 오기 시작했는지
    private Boolean arrived; //안읽은 편지 있는지
    private Integer arrivedCount; //안읽은 편지 수
}
