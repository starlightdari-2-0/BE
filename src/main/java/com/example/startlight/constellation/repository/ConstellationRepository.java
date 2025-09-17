package com.example.startlight.constellation.repository;

import com.example.startlight.constellation.entity.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, Long> {

    @Query("select c from Constellation c where c.animalType.animal_type_id = :animalTypeId")
    List<Constellation> findConstellationByAnimalTypeId(Long animalTypeId);
}
