package com.example.demo.repository;

import com.example.demo.entity.Historique;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface HistoriqueRepo extends JpaRepository<Historique, Long> {
    boolean existsByTel(Long tel);

    int countByTelAndOfferId(Long tel, Long offerId);

    int countByTelAndOfferIdAndDateBetween(Long tel, Long offreId, LocalDateTime since, LocalDateTime now);

    Historique findTopByTelAndOfferIdOrderByDateDesc(Long tel, Long offerId);

    List<Historique> findByTelAndOfferIdAndDateAfterOrderByDateAsc(Long tel, Long offerId, LocalDateTime minus);

    @Transactional
    @Modifying
    @Query("DELETE FROM Historique u WHERE u.tel = :tel")
    void removeByTel(@Param("tel") Long l);
}
