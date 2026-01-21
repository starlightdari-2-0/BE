package com.example.startlight.community.funeral.repository;

import com.example.startlight.community.funeral.entity.Funeral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuneralRepository extends JpaRepository<Funeral, Long> {
    List<Funeral> findByNameContainingIgnoreCase(String name);
}
