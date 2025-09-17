package com.example.startlight.constellation.controller;

import com.example.startlight.constellation.dto.ConstellationResponseDto;
import com.example.startlight.constellation.entity.Constellation;
import com.example.startlight.constellation.service.ConstellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("constellation")
public class ConstellationController {
    private final ConstellationService constellationService;

    @GetMapping("/{animalTypeId}")
    private ResponseEntity<List<ConstellationResponseDto>> getConstellation(@PathVariable Long animalTypeId) {
        List<ConstellationResponseDto> constellationByAnimalType = constellationService.getConstellationByAnimalType(animalTypeId);
        return ResponseEntity.status(HttpStatus.OK).body(constellationByAnimalType);
    }
}
