package com.example.startlight.pet.service;

import com.example.startlight.pet.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PetService {
    PetIdRepDto createPet(PetReqDto petReqDto) throws IOException;
    PetRepDto updatePet(Long petId, PetUpdateReqDto petUpdateReqDto);
    List<PetMyPageRepDto> getPets();
    List<PetSimpleRepDto> getPetSimple(Long userId);
    PetStarListRepDto getPetStarList(Long petId) throws AccessDeniedException;
    void deletePet(Long petId);
}
