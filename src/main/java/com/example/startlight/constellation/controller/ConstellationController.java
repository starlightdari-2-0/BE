package com.example.startlight.constellation.controller;

import com.example.startlight.constellation.dto.ConstellationResponseDto;
import com.example.startlight.constellation.dto.ConstellationWithStarRepDto;
import com.example.startlight.constellation.entity.Constellation;
import com.example.startlight.constellation.service.ConstellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadConstellation(
            @RequestParam("conId") Long conId,
            @RequestParam("majorPoints") MultipartFile majorPointsFile,
            @RequestParam("edges") MultipartFile edgesFile) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 파일 검증
            if (majorPointsFile.isEmpty() || edgesFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "파일이 비어있습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 임시 파일 생성
            File majorPointsTempFile = File.createTempFile("major_points", ".json");
            File edgesTempFile = File.createTempFile("edges", ".json");

            // 파일 저장
            majorPointsFile.transferTo(majorPointsTempFile);
            edgesFile.transferTo(edgesTempFile);

            // 서비스 호출 - JSON 파싱 및 DB 저장
            constellationService.saveConstellationFromJson(
                    conId,
                    majorPointsTempFile.getAbsolutePath(),
                    edgesTempFile.getAbsolutePath()
            );

            // 임시 파일 삭제
            majorPointsTempFile.delete();
            edgesTempFile.delete();

            response.put("success", true);
            response.put("message", "별자리 데이터가 성공적으로 저장되었습니다.");
            response.put("constellationId", conId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "저장 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/load-from-file")
    public ResponseEntity<Map<String, Object>> loadFromFile(
            @RequestParam("conId") Long conId,
            @RequestParam("majorPointsPath") String majorPointsPath,
            @RequestParam("edgesPath") String edgesPath) {

        Map<String, Object> response = new HashMap<>();

        try {
            constellationService.saveConstellationFromJson(conId, majorPointsPath, edgesPath);

            response.put("success", true);
            response.put("message", "별자리 데이터가 성공적으로 저장되었습니다.");
            response.put("constellationId", conId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "저장 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/each/{conId}")
    public ResponseEntity<ConstellationWithStarRepDto> getConStellationWithStar(@PathVariable Long conId) {
        ConstellationWithStarRepDto constellationWithStar = constellationService.getConstellationById(conId);
        return ResponseEntity.ok(constellationWithStar);
    }

        /**
         * 특정 노드 조회 (복합키 사용)
         * GET /api/constellation/node/{conId}/{nodeId}
         */
//        @GetMapping("/node/{conId}/{nodeId}")
//        public ResponseEntity<?> getNode(
//                @PathVariable Long conId,
//                @PathVariable Long nodeId) {
//
//            try {
//                StarNodeId id = new StarNodeId(conId, nodeId);
//                StarNode node = constellationService.findNode(id);
//                return ResponseEntity.ok(node);
//
//            } catch (IllegalArgumentException e) {
//                Map<String, String> error = new HashMap<>();
//                error.put("error", e.getMessage());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//            }
//        }

        /**
         * 특정 별자리의 모든 노드 조회
         * GET /api/constellation/nodes/{conId}
         */
//        @GetMapping("/nodes/{conId}")
//        public ResponseEntity<List<StarNode>> getNodesByConstellation(
//                @PathVariable Long conId) {
//
//            List<StarNode> nodes = constellationService.findAllNodesByConstellation(conId);
//            return ResponseEntity.ok(nodes);
//        }

        /**
         * 특정 별자리의 모든 엣지 조회
         * GET /api/constellation/edges/{conId}
         */
//        @GetMapping("/edges/{conId}")
//        public ResponseEntity<List<StarEdge>> getEdgesByConstellation(
//                @PathVariable Long conId) {
//
//            List<StarEdge> edges = constellationService.findAllEdgesByConstellation(conId);
//            return ResponseEntity.ok(edges);
//        }

        /**
         * 특정 별자리의 전체 데이터 조회 (노드 + 엣지)
         * GET /api/constellation/{conId}
         */
//        @GetMapping("/{conId}")
//        public ResponseEntity<Map<String, Object>> getConstellationData(
//                @PathVariable Long conId) {
//
//            try {
//                Map<String, Object> data = new HashMap<>();
//
//                List<StarNode> nodes = constellationService.findAllNodesByConstellation(conId);
//                List<StarEdge> edges = constellationService.findAllEdgesByConstellation(conId);
//
//                data.put("constellationId", conId);
//                data.put("nodes", nodes);
//                data.put("edges", edges);
//                data.put("nodeCount", nodes.size());
//                data.put("edgeCount", edges.size());
//
//                return ResponseEntity.ok(data);
//
//            } catch (Exception e) {
//                Map<String, String> error = new HashMap<>();
//                error.put("error", "데이터 조회 중 오류 발생: " + e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//            }
//        }

        /**
         * 특정 별자리 데이터 삭제
         * DELETE /api/constellation/{conId}
         */
//        @DeleteMapping("/{conId}")
//        public ResponseEntity<Map<String, Object>> deleteConstellationData(
//                @PathVariable Long conId) {
//
//            Map<String, Object> response = new HashMap<>();
//
//            try {
//                constellationService.deleteConstellationData(conId);
//
//                response.put("success", true);
//                response.put("message", "별자리 데이터가 삭제되었습니다.");
//                response.put("constellationId", conId);
//
//                return ResponseEntity.ok(response);
//
//            } catch (Exception e) {
//                response.put("success", false);
//                response.put("message", "삭제 중 오류 발생: " + e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//            }
//        }

}
