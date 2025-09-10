package com.example.startlight.constellation.repository;

import com.example.startlight.constellation.entity.StarNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarNodeRepository extends JpaRepository<StarNode, Long> {
}
