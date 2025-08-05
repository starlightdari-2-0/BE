package com.example.startlight.memoryAlbum.controller;

import com.example.startlight.memoryAlbum.dto.*;
import com.example.startlight.memoryAlbum.service.MemoryAlbumFlaskService;
import com.example.startlight.memoryAlbum.service.MemoryAlbumScheduleService;
import com.example.startlight.memoryAlbum.service.MemoryAlbumService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("memory-album")
public class MemoryAlbumController {
    private final MemoryAlbumFlaskService memoryAlbumFlaskService;
    private final MemoryAlbumScheduleService memoryAlbumScheduleService;
    private final MemoryAlbumService memoryAlbumService;

    @GetMapping()
    public ResponseEntity<?> createMemoryAlbum() {
        memoryAlbumFlaskService.generateMemoryAlbum(52L,0);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<List<AlbumByPetRepDto>> getAllMemoryAlbumStatus() {
        List<AlbumByPetRepDto> memoryAlbumStatusByPet = memoryAlbumService.getMemoryAlbumStatusByPet();
        return ResponseEntity.ok(memoryAlbumStatusByPet);
    }

    @PostMapping("/test")
    public ResponseEntity<?> testMemoryAlbum() {
        memoryAlbumScheduleService.createAlbumAfterOneDay(52L);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MemoryAlbumSimpleDto>> getMemoryAlbumByPetId(@PathVariable("petId") Long petId) {
        List<MemoryAlbumSimpleDto> memoryAlbumByPet = memoryAlbumService.getMemoryAlbumByPet(petId);
        return ResponseEntity.status(HttpStatus.OK).body(memoryAlbumByPet);
    }

    @GetMapping("/letter/{letterId}")
    public ResponseEntity<MemoryAlbumRepDto> getMemoryAlbumByLetterId(@PathVariable("letterId") Long letterId) {
        MemoryAlbumRepDto memoryAlbum = memoryAlbumService.getMemoryAlbumAndUpdateRead(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(memoryAlbum);
    }

    @PostMapping()
    public ResponseEntity<MemoryAlbumRepDto> addMemoryAlbum() {
        // JSON 매핑을 위한 ObjectMapper 사용 예제
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonResponse = "{\n" +
                "  \"images\": [\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_0.png\",\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_1.png\",\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_2.png\",\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_3.png\",\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_4.png\",\n" +
                "    \"https://starlightbucket.s3.amazonaws.com/letters/102/generated_image_5.png\"\n" +
                "  ],\n" +
                "  \"letter\": \"안녕, None!\\n\\n이제 하늘나라에서 쭉 자유롭게 뛰어놀고 있어...\",\n" +
                "  \"title\": \"추억의 편지\"\n" +
                "}";

        try {
            // JSON -> LetterGeneratedRepDto 매핑
            LetterGeneratedFileRepDto responseDto = objectMapper.readValue(jsonResponse, LetterGeneratedFileRepDto.class);

            // 매핑된 값 확인
            System.out.println("Title: " + responseDto.getTitle());
            System.out.println("Letter: " + responseDto.getLetter());
            System.out.println("Images: " + responseDto.getImages());

            MemoryAlbumRepDto memoryAlbum = memoryAlbumService.createMemoryAlbum(102L, responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(memoryAlbum);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomMemoryAlbum() {
        memoryAlbumFlaskService.generateMemoryAlbum(102L,3);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/birth")
    public ResponseEntity<?> getBirthMemoryAlbum() {
        memoryAlbumFlaskService.generateMemoryAlbum(102L,1);
        return ResponseEntity.ok().build();
    }
}
