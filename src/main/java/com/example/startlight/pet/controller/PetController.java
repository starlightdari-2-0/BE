package com.example.startlight.pet.controller;

import com.example.startlight.pet.dto.*;
import com.example.startlight.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
@Slf4j
public class PetController {
    private final PetService petService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetIdRepDto> createPet(
            @ModelAttribute PetReqDto petReqDto
    ) throws IOException {
        PetIdRepDto responsePetRepDto = petService.createPet(petReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(responsePetRepDto);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<PetRepDto> updatePet(@PathVariable Long petId, @RequestBody PetReqDto petUpdateReqDto) throws IOException {
        PetRepDto responsePetRepDto = petService.updatePet(petId, petUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(responsePetRepDto);
    }

    @GetMapping()
    public ResponseEntity<List<PetMyPageRepDto>> getAllPets() {
        List<PetMyPageRepDto> petRepDtoList = petService.getPets();
        return ResponseEntity.status(HttpStatus.OK).body(petRepDtoList);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetRepDto> getEachPet(@PathVariable Long petId) throws IOException {
        PetRepDto petRepDto = petService.getPetById(petId);
        return ResponseEntity.status(HttpStatus.OK).body(petRepDto);
    }

//    @GetMapping("/{petId}/stars")
//    public ResponseEntity<PetStarListRepDto> getList(@PathVariable Long petId) {
//        try {
//            PetStarListRepDto petStarList = petService.getPetStarList(petId);
//            return ResponseEntity.status(HttpStatus.OK).body(petStarList);
//        } catch (AccessDeniedException e) {
//            // ✅ 접근 권한 없을 경우 403 반환
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(null);
//        }
//    }

    @GetMapping("/{petId}/stars")

    @DeleteMapping("/{petId}")
    public ResponseEntity<String> deletePet(@PathVariable Long petId) throws AccessDeniedException {
        petService.deletePet(petId);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted pet with id " + petId);
    }
}
