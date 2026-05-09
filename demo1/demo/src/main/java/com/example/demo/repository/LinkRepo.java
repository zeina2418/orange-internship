package com.example.demo.repository;

import com.example.demo.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepo extends JpaRepository<Link, Long> {
    List<Link> findAll();
    @Query("SELECT l.offerId FROM Link l WHERE l.segment = :segment")
    List<Long> findOfferIdsBySegment(@Param("segment") Long segment);

    boolean existsByOfferIdAndSegment(Long idOffre, Long seg);
}
