package com.example.startlight.constellation.service;

import com.example.startlight.constellation.dto.ConstellationResponseDto;
import com.example.startlight.constellation.entity.Constellation;
import com.example.startlight.constellation.repository.ConstellationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConstellationService {
    
    private final ConstellationRepository constellationRepository;

    public List<ConstellationResponseDto> getConstellationByAnimalType(Long animalTypeId) {
        List<Constellation> constellations = constellationRepository.findConstellationByAnimalTypeId(animalTypeId);
        return constellations.stream()
                .map(constellation -> ConstellationResponseDto.builder()
                        .con_id(constellation.getCon_id())
                        .code(constellation.getCode())
                        .thumbnail_img(constellation.getThumbnail_img())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
