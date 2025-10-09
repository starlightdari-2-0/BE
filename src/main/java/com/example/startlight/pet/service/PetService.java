package com.example.startlight.pet.service;

import com.example.startlight.pet.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PetService {
    PetIdRepDto createPet(PetReqDto petReqDto) throws IOException;
    PetRepDto updatePet(Long petId, PetReqDto petUpdateReqDto) throws IOException;
    PetRepDto getPetById(Long petId) throws IOException;
    List<PetMyPageRepDto> getPets();
    List<PetSimpleRepDto> getPetSimple(Long userId);
    String getPetName(Long petId);
    Long getPetConId(Long petId);
    void getPetStars(Long petId);
    void deletePet(Long petId) throws AccessDeniedException;
}
