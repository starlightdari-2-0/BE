package com.example.startlight.community.funeral.dao;

import com.example.startlight.community.funeral.entity.Funeral;
import com.example.startlight.community.funeral.repository.FuneralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class FuneralDao {
    private final FuneralRepository funeralRepository;

    public Funeral selectById(Long id) {
        return funeralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funeral not found with id: " + id));
    }

    public List<Funeral> findByName(String name) {
        return funeralRepository.findByNameContainingIgnoreCase(name);
    }
}
