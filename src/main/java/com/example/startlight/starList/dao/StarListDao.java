package com.example.startlight.starList.dao;

import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.starList.entity.StarList;
import com.example.startlight.starList.repository.StarListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StarListDao {

    private final StarListRepository starListRepository;

    public List<StarList> createStarList(List<List<Integer>> majorPoints, Pet selectedPet) {
        List<StarList> starLists = majorPoints.stream()
                .map(point -> StarList.builder()
                        .pet(selectedPet)
                        .x_star(point.get(0))
                        .y_star(point.get(1))
                        .index_id((Integer) majorPoints.indexOf(point))
                        .written(false)
                        .build())
                .collect(Collectors.toList());
        return starListRepository.saveAll(starLists);
    }

    public List<StarList> findAllStarList(Long petId) {
        return starListRepository.findByPetId(petId);
    }

    public StarList findStarListById(Long id) {
        return starListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("❌ StarList가 존재하지 않습니다. ID: " + id));
    }

    @Transactional
    public void updateStarWritten(MemoryStar createdStar, Long id) {
        StarList starList = findStarListById(id);
        starList.updateStarWritten(createdStar);

        // ✅ 성공 로그 출력
        log.info("✅ StarList [{}] - 상태: written 설정 완료", id);
    }

    @Transactional
    public void setStarUnWritten(Long id) {
        StarList starList = findStarListById(id);
        starList.updateStarUnWritten();

        // ✅ 성공 로그 출력
        log.info("✅ StarList [{}] - 상태: unwritten 설정 완료", id);
    }
}
