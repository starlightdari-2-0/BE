package com.example.startlight.constellation.service;

import com.example.startlight.constellation.dto.*;
import com.example.startlight.constellation.entity.Constellation;
import com.example.startlight.constellation.entity.StarEdge;
import com.example.startlight.constellation.entity.StarNode;
import com.example.startlight.constellation.repository.ConstellationRepository;
import com.example.startlight.constellation.repository.StarEdgeRepository;
import com.example.startlight.constellation.repository.StarNodeRepository;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.dto.PetStarRepDto;
import com.example.startlight.constellation.dto.StarNodeWithMemoryDto;
import com.example.startlight.pet.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConstellationService {
    
    private final ConstellationRepository constellationRepository;
    private final StarNodeRepository starNodeRepository;
    private final StarEdgeRepository starEdgeRepository;
    private final MemoryStarRepository memoryStarRepository;
    private final PetService petService;
    private final ObjectMapper objectMapper;

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

    public void saveConstellationFromJson(Long conId, String majorPointsPath, String edgesPath)
            throws IOException {

        // 1. major_points.json 파싱 및 저장
        MajorPointsJson majorPointsJson = objectMapper.readValue(
                new File(majorPointsPath),
                MajorPointsJson.class
        );

        List<StarNodeDto> nodeList = new ArrayList<>();
        for (int i = 0; i < majorPointsJson.getMajorPoints().size(); i++) {
            List<Integer> point = majorPointsJson.getMajorPoints().get(i);
            StarNodeDto dto = StarNodeDto.builder()
                    .node_id((long) (i + 1))
                    .con_id(conId)
                    .x_star(point.get(0))
                    .y_star(point.get(1))
                    .build();
            nodeList.add(dto);
        }

        saveNodes(nodeList);

        // 2. edges.json 파싱 및 저장
        EdgesJson edgesJson = objectMapper.readValue(
                new File(edgesPath),
                EdgesJson.class
        );

        List<StarEdgeDto> edgeList = new ArrayList<>();
        for (int i = 0; i < edgesJson.getEdges().size(); i++) {
            List<Integer> edge = edgesJson.getEdges().get(i);
            StarEdgeDto dto = StarEdgeDto.builder()
                    .con_id(conId)
                    .start_node_id(edge.get(0).longValue() + 1)
                    .end_node_id(edge.get(1).longValue() + 1)
                            .build();
            edgeList.add(dto);
        }

        saveEdges(edgeList);
    }

    /**
     * Node 리스트를 DB에 저장
     */
    public void saveNodes(List<StarNodeDto> nodeList) {
        Optional<Constellation> byId = constellationRepository.findById(nodeList.get(0).getCon_id());
        if (byId.isPresent()) {
            Constellation constellation = byId.get();
            List<StarNode> entities = nodeList.stream()
                    .map(dto -> StarNode.builder()
                            .node_id(dto.getNode_id())
                            .constellation(constellation)
                            .x_star(dto.getX_star())
                            .y_star(dto.getY_star())
                                    .build())
                    .collect(Collectors.toList());

            starNodeRepository.saveAll(entities);
        }
    }

    /**
     * Edge 리스트를 DB에 저장
     */
    public void saveEdges(List<StarEdgeDto> edgeList) {
        Optional<Constellation> byId = constellationRepository.findById(edgeList.get(0).getCon_id());
        if (byId.isPresent()) {
            Constellation constellation = byId.get();
            List<StarEdge> entities = edgeList.stream()
                    .map(dto -> StarEdge.builder()
                            .constellation(constellation)
                            .start_node_id(dto.getStart_node_id())
                            .end_node_id(dto.getEnd_node_id())
                            .build())
                    .collect(Collectors.toList());

            starEdgeRepository.saveAll(entities);
        }

    }

    public ConstellationWithStarRepDto getConstellationById(Long conId) {
        Optional<Constellation> optionalConstellation = constellationRepository.findById(conId);
        if (optionalConstellation.isPresent()) {
            Constellation constellation = optionalConstellation.get();
            List<StarEdge> starEdgeList = starEdgeRepository.findByConstellationId(conId);
            List<StarNode> starNodeList = starNodeRepository.findByConstellationId(conId);

            List<StarEdgeRepDto> starEdgeDtoList = starEdgeList.stream()
                    .map(starEdge -> StarEdgeRepDto.builder()
                            .startPoint(starEdge.getStart_node_id())
                            .endPoint(starEdge.getEnd_node_id())
                            .build())
                    .toList();

            List<StarNodeRepDto> starNodeDtoList = starNodeList.stream()
                    .map(starNode -> StarNodeRepDto.builder()
                            .star_node_id(starNode.getStar_node_id())
                            .node_id(starNode.getNode_id())
                            .x_star(starNode.getX_star())
                            .y_star(starNode.getY_star())
                            .build()).toList();
            return ConstellationWithStarRepDto.builder()
                    .con_id(conId)
                    .thumbnail_img(constellation.getThumbnail_img())
                    .nodes(starNodeDtoList)
                    .edges(starEdgeDtoList)
                    .build();
        }
        return null;
    }

    public PetStarRepDto getConstellationWithStarByPetId(Long petId) {
        Long conId = petService.getPetConId(petId);
        Optional<Constellation> optionalConstellation = constellationRepository.findById(conId);
        if (optionalConstellation.isPresent()) {
            String petName = petService.getPetName(petId);
            Constellation constellation = optionalConstellation.get();
            List<StarEdge> starEdgeList = starEdgeRepository.findByConstellationId(conId);
            List<StarNode> starNodeList = starNodeRepository.findByConstellationId(conId);

            List<StarEdgeRepDto> starEdgeDtoList = starEdgeList.stream()
                    .map(starEdge -> StarEdgeRepDto.builder()
                            .startPoint(starEdge.getStart_node_id())
                            .endPoint(starEdge.getEnd_node_id())
                            .build())
                    .toList();

            List<StarNodeWithMemoryDto> starNodeDtoList = starNodeList.stream()
                    .map(starNode -> StarNodeWithMemoryDto.builder()
                            .star_node_id(starNode.getStar_node_id())
                            .node_id(starNode.getNode_id())
                            .x_star(starNode.getX_star())
                            .y_star(starNode.getY_star())
                            .build()).toList();
            for (StarNodeWithMemoryDto starNodeWithMemoryDto : starNodeDtoList) {
                Long memoryId = memoryStarRepository.findByPetIdAndStarNodeId(petId, starNodeWithMemoryDto.getStar_node_id());
                if (memoryId != null) {
                    starNodeWithMemoryDto.setMemoryWritten(memoryId);
                }
            }

            return PetStarRepDto.builder()
                    .petId(petId)
                    .petName(petName)
                    .thumbnail_img(constellation.getThumbnail_img())
                    .nodes(starNodeDtoList)
                    .edges(starEdgeDtoList).build();
        }
        return null;
    }
}
