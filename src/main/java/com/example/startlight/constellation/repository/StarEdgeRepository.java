package com.example.startlight.constellation.repository;

import com.example.startlight.constellation.entity.StarEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarEdgeRepository extends JpaRepository<StarEdge, Long> {

    @Query("select e from StarEdge e where e.constellation.con_id = :conId")
    List<StarEdge> findByConstellationId(@Param("conId") Long conId);
}
