package com.example.demo.repository;

import com.example.demo.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface OffreRepo extends JpaRepository<Offre, Long> {

    Offre findBySegment(Long seg);

    boolean existsBySegment(Long seg);

    List<Offre> findByIdIn(List<Long> ids);

}
