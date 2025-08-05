package com.example.startlight.funeral.repository;

import com.example.startlight.funeral.entity.Funeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FuneralRepository extends JpaRepository<Funeral, Long> {
    List<Funeral> findByNameContainingIgnoreCase(String name);
}
