package com.example.startlight.starInfo.repository;

import com.example.startlight.starInfo.entity.StarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StarInfoRepository extends JpaRepository<StarInfo, Integer> {

    @Query("select s from StarInfo s where s.con_id = :con_id and s.node_id = :node_id")
    Long findMemoryId(@Param("con_id") Long con_id, @Param("node_id") Long node_ic);
}
