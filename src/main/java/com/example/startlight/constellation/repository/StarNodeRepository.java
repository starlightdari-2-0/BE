package com.example.startlight.constellation.repository;

import com.example.startlight.constellation.entity.StarNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarNodeRepository extends JpaRepository<StarNode, Long> {

    @Query("select n from StarNode n where n.con_id = :conId")
    List<StarNode> findByConstellationId(@Param("conId") Long conId);
}
