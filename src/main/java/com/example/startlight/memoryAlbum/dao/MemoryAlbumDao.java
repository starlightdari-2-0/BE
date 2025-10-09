package com.example.startlight.memoryAlbum.dao;

import com.example.startlight.memoryAlbum.entity.MemoryAlbum;
import com.example.startlight.memoryAlbum.repository.MemoryAlbumRepository;
import com.example.startlight.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemoryAlbumDao {
    private final MemoryAlbumRepository memoryAlbumRepository;
    private final PetRepository petRepository;
    private final MemoryStarRepository memoryStarRepository;

    public MemoryAlbum createMemoryAlbum(MemoryAlbum memoryAlbum) {
        return memoryAlbumRepository.save(memoryAlbum);
    }

    public List<MemoryAlbum> findByPetId(Long petId) {
        return memoryAlbumRepository.findAllByPetId(petId);
    }

    public MemoryAlbum findById(Long id) {
        return memoryAlbumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));
    }

//    public List<String> getRecent5ImgsByPetId(Long petId) {
//        Pageable pageable = (Pageable) PageRequest.of(0, 5);
//        return memoryStarRepository.findPetImagesByPetId(petId, pageable);
//    }


}
