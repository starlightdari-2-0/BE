package com.example.startlight.constellation.service;

import com.example.startlight.constellation.dto.*;
import com.example.startlight.constellation.entity.Constellation;
import com.example.startlight.constellation.entity.StarEdge;
import com.example.startlight.constellation.entity.StarNode;
import com.example.startlight.constellation.repository.ConstellationRepository;
import com.example.startlight.constellation.repository.StarEdgeRepository;
import com.example.startlight.constellation.repository.StarNodeRepository;
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

        List<StarNodeRequestDto> nodeList = new ArrayList<>();
        for (int i = 0; i < majorPointsJson.getMajorPoints().size(); i++) {
            List<Integer> point = majorPointsJson.getMajorPoints().get(i);
            StarNodeRequestDto dto = new StarNodeRequestDto(
                    (long) (i + 1),  // node_id는 1부터 시작
                    conId,
                    point.get(0),    // x 좌표
                    point.get(1)     // y 좌표
            );
            nodeList.add(dto);
        }

        saveNodes(nodeList);

        // 2. edges.json 파싱 및 저장
        EdgesJson edgesJson = objectMapper.readValue(
                new File(edgesPath),
                EdgesJson.class
        );

        List<StarEdgeRequestDto> edgeList = new ArrayList<>();
        for (int i = 0; i < edgesJson.getEdges().size(); i++) {
            List<Integer> edge = edgesJson.getEdges().get(i);
            StarEdgeRequestDto dto = new StarEdgeRequestDto(
                    (long) (i + 1),          // edge_id는 1부터 시작
                    conId,
                    edge.get(0).longValue() + 1,  // start_node_id (인덱스 0부터 시작하므로 +1)
                    edge.get(1).longValue() + 1   // end_node_id (인덱스 0부터 시작하므로 +1)
            );
            edgeList.add(dto);
        }

        saveEdges(edgeList);
    }

    /**
     * Node 리스트를 DB에 저장
     */
    public void saveNodes(List<StarNodeRequestDto> nodeList) {
        Optional<Constellation> byId = constellationRepository.findById(nodeList.get(0).getCon_id());
        if (byId.isPresent()) {
            Constellation constellation = byId.get();
            List<StarNode> entities = nodeList.stream()
                    .map(dto -> new StarNode(
                            dto.getCon_id(),
                            dto.getNode_id(),
                            constellation,
                            dto.getX_star(),
                            dto.getY_star()
                    ))
                    .collect(Collectors.toList());

            starNodeRepository.saveAll(entities);
        }
    }

    /**
     * Edge 리스트를 DB에 저장
     */
    public void saveEdges(List<StarEdgeRequestDto> edgeList) {
        Optional<Constellation> byId = constellationRepository.findById(edgeList.get(0).getCon_id());
        if (byId.isPresent()) {
            Constellation constellation = byId.get();
            List<StarEdge> entities = edgeList.stream()
                    .map(dto -> new StarEdge(
                            dto.getEdge_id(),
                            constellation,
                            dto.getStart_node_id(),
                            dto.getEnd_node_id()
                    ))
                    .collect(Collectors.toList());

            starEdgeRepository.saveAll(entities);
        }

    }
}
